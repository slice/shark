package zone.slice.shark

import cats.effect._
import cats.implicits._

object Shark extends IOApp {
  def program[F[_]: ContextShift: Concurrent]: F[Unit] =
    Blocker[F].use { blocker =>
      val server = new Server(blocker)
      server.run
    }

  def run(args: List[String]): IO[ExitCode] =
    program[IO].as(ExitCode.Success)
}
