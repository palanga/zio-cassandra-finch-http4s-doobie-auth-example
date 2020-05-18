package thewho.auth

import io.circe.Decoder
import io.circe.parser.decode
import thewho.error.AuthException.AuthDefect.CirceDecodeDefect
import zio.{ IO, ZIO }

// TODO move to utils zio circe
object CirceZioAdapter {

  def decodeString[A: Decoder](input: String): IO[CirceDecodeDefect, A] =
    ZIO fromEither decode[A](input) mapError CirceDecodeDefect

}
