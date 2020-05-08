package zone.slice.shark

import cats.effect._
import cats.implicits._
import fs2.io.tcp.Socket

import java.util.UUID

case class Fish[F[_]](id: UUID, socket: Socket[F]) {
  def name: String = {
    def pick[A](as: Seq[A]) = as(id.hashCode % (as.size - 1))

    val adjective = pick(Fish.adjectives)
    val noun      = pick(Fish.nouns)
    s"${adjective} ${noun}"
  }
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

  def make[F[_]: Sync](socket: Socket[F]): F[Fish[F]] =
    Sync[F].delay(UUID.randomUUID()).map { uuid => Fish(uuid, socket) }
}
