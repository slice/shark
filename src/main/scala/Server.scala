package zone.slice.shark

import cats.syntax.all._
import cats.effect.Resource
import cats.effect.Async
import cats.effect.std.Console
import fs2.Stream
import fs2.io.net.{Network, Socket}
import com.comcast.ip4s._

import java.nio.charset.Charset

class Server[F[_]: Async: Console: Network] {
  private def whoops(throwable: Throwable): Stream[F, Nothing] =
    Stream.exec(
      Console[F].errorln(
        s"oh noes something broke!! :3\n\n${throwable.getStackTrace}",
      ),
    )

  private def connectMessage(fish: Fish[F]): String =
    Colors.blue(s"[server] ${fish.name} connected\n")
  private def disconnectMessage(fish: Fish[F]): String =
    Colors.blue(s"[server] ${fish.name} left\n")

  private def createFish(
      state: State[F],
      socket: Socket[F],
  ): Resource[F, Fish[F]] =
    for {
      fish <- Resource.make(
        Fish.make[F](socket).flatTap(state.add),
      )(fish => state.remove(fish) *> state.broadcast(disconnectMessage(fish)))
      _ <- Resource.eval(state.broadcast(connectMessage(fish)))
    } yield fish

  private def handleFish(state: State[F], fish: Fish[F]): Stream[F, Unit] =
    fish.socket.reads
      .through(fs2.text.utf8.decode)
      .through(fs2.text.lines)
      .filter(!_.trim.isEmpty)
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

  def run: F[Unit] =
    Stream.eval(State.make).flatMap { state => 
      Network[F]
        .server(port = port"3333".some)
        .map { socket =>
          for {
            fish <- Stream.resource(createFish(state, socket))
            _    <- handleFish(state, fish).handleErrorWith(whoops(_))
          } yield ()
        }
        .parJoin(1024)
    }.compile.drain
}

object Server {
  val charset = Charset.forName("UTF-8")
}
