package zone.slice.shark

import cats.effect.{IO, IOApp}
import cats.effect.Async
import cats.effect.std.Console

object Shark extends IOApp.Simple {
  def program[F[_]: Async: Console]: F[Unit] =
    new Server[F].run

  def run: IO[Unit] =
    program[IO]
}
