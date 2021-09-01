package zone.slice.shark

import cats.syntax.all._
import cats.effect.Sync
import cats.effect.std.Random

import java.util.UUID
import fs2.io.net.Socket
import fs2.Chunk

case class Fish[F[_]](id: UUID, socket: Socket[F]) {
  def name: String = {
    def pick[A](as: Seq[A]) = as(id.hashCode.abs % (as.size - 1))

    val adjective = pick(Fish.adjectives)
    val noun      = pick(Fish.nouns)
    s"${adjective} ${noun}"
  }

  def send(message: String): F[Unit] =
    socket.write(Chunk.array(message.getBytes(Server.charset)))
}

object Fish {
  val adjectives = List(
    "red",
    "orange",
    "yellow",
    "green",
    "blue",
    "purple",
    "magenta",
    "violet",
    "brown",
    "white",
    "black",
  )

  val nouns = List(
    "sphere",
    "circle",
    "square",
    "cube",
    "prism",
    "pyramid",
    "cylinder",
    "triangle",
    "hexagon",
    "octagon",
    "pentagon",
    "rhombus",
    "nonagon",
    "ellipse",
  )

  private def uuid[F[_]: Sync]: F[UUID] =
    Sync[F].delay(UUID.randomUUID())

  def make[F[_]: Sync](socket: Socket[F]): F[Fish[F]] =
    uuid.map(Fish(_, socket))
}
