package zone.slice.shark

import cats._
import cats.effect._
import cats.implicits._
import cats.effect.concurrent.Ref

import java.util.UUID
import java.nio.charset.Charset
import fs2.Chunk

case class State[F[_]: Monad](ref: Ref[F, Map[UUID, Fish[F]]]) {
  private val charset = Charset.forName("UTF-8")

  def add(fish: Fish[F]): F[Unit] =
    ref.update(map => map + (fish.id -> fish))
  def remove(fish: Fish[F]): F[Unit] =
    ref.update(map => map - fish.id)
  def broadcast(message: String): F[Unit] =
    ref.get.flatMap { map =>
      map.values.toVector
        .traverse_(_.socket.write(Chunk.bytes(message.getBytes(charset))))
    }
}

object State {
  def make[F[_]: Sync]: F[State[F]] =
    Ref[F].of(Map[UUID, Fish[F]]()).map(State(_))
}
