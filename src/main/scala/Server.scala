package zone.slice.shark

import fs2.Stream
import cats.effect._
import cats.implicits._
import fs2.io.tcp.SocketGroup
import fs2.text

import java.net.InetSocketAddress
import fs2.io.tcp.Socket
import java.nio.charset.Charset

class Server[F[_]: Concurrent: ContextShift](blocker: Blocker) {
  private def handleClient(state: State[F], fish: Fish[F]): Stream[F, Unit] =
    fish.socket
      .reads(8192)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(_.trim != "")
      .evalMap {
        case "/list" =>
          state.ref.get.flatMap { map =>
            val listing = map.values.map(_.name) mkString ", "
            val message = s"[server] users (${map.size}): $listing\n"
            fish.send(Colors.green(message))
          }
        case message =>
          state.broadcast(s"[${fish.name}] $message\n")
      }

  def `don't care`(throwable: Throwable): Stream[F, Nothing] =
    Stream
      .eval_(Sync[F].delay {
        Console.err.println(
          s"something broke!! :3\n\n${throwable.getStackTrace}",
        )
      })

  def stream(socketGroup: SocketGroup): Stream[F, Unit] =
    (for {
      state  <- Stream.eval(State.make)
      socket <- socketGroup.server(new InetSocketAddress(3333))
    } yield for {
      client <- Stream.resource(socket)
      fish <-
        Stream
          .bracket(Fish.make[F](client).flatTap(state.add))(fish =>
            state.remove(fish) *> state
              .broadcast(
                Colors.blue(s"[server] ${fish.name} has disconnected\n"),
              ),
          )
      _ <- Stream.eval(
        state.broadcast(Colors.blue(s"[server] ${fish.name} has connected\n")),
      )
      _ <- handleClient(state, fish).handleErrorWith(`don't care`)
    } yield ()).parJoin(1024)

  def run: F[Unit] =
    SocketGroup[F](blocker).use { socketGroup =>
      stream(socketGroup).compile.drain
    }
}

object Server {
  val charset = Charset.forName("UTF-8")
}
