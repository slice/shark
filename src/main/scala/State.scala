package zone.slice.shark

import cats._
import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Ref

import java.util.UUID
import fs2.Chunk

case class State[F[_]: Monad](ref: Ref[F, Map[UUID, Fish[F]]]) {
  def add(fish: Fish[F]): F[Unit] =
    ref.update(map => map + (fish.id -> fish))
  def remove(fish: Fish[F]): F[Unit] =
    ref.update(map => map - fish.id)
  def broadcast(bytes: Array[Byte]): F[Unit] =
    ref.get.flatMap { map =>
      map.values.toVector
        .traverse_(_.socket.write(Chunk.bytes(bytes)))
    }
}

object State {
  def make[F[_]: Sync]: F[State[F]] =
    Ref[F].of(Map[UUID, Fish[F]]()).map(State(_))
}
