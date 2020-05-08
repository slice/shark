package zone.slice.shark

import scala.io.AnsiColor

object Colors {
  def surround(
      begin: String,
      message: String,
      end: String = AnsiColor.RESET,
  ): String =
    begin + message + end

  def red(message: String): String     = surround(AnsiColor.RED, message)
  def yellow(message: String): String  = surround(AnsiColor.YELLOW, message)
  def green(message: String): String   = surround(AnsiColor.GREEN, message)
  def blue(message: String): String    = surround(AnsiColor.BLUE, message)
  def magenta(message: String): String = surround(AnsiColor.MAGENTA, message)
}
