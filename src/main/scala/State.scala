package zone.slice.shark

import cats.Monad
import cats.effect.Sync
import cats.syntax.all._
import cats.effect.Ref

import java.util.UUID
import fs2.Chunk

case class State[F[_]: Monad](ref: Ref[F, Map[UUID, Fish[F]]]) {
  def add(fish: Fish[F]): F[Unit] =
    ref.update(_ + (fish.id -> fish))

  def remove(fish: Fish[F]): F[Unit] =
    ref.update(_ - fish.id)

  def broadcast(message: String): F[Unit] =
    ref.get.flatMap(
      _.values.toVector
        .traverse_(
          _.socket.write(Chunk.array(message.getBytes(Server.charset))),
        ),
    )
}

object State {
  def make[F[_]: Sync]: F[State[F]] =
    Ref[F].of(Map[UUID, Fish[F]]()).map(State(_))
}
