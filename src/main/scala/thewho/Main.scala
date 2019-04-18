package thewho

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder
import scalaz.zio.DefaultRuntime
import thewho.http.AuthHttpService.authHttpService

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends StreamApp[IO] {

  val runtime = new DefaultRuntime {}

  val server = BlazeBuilder[IO]
    .bindHttp(8080, "localhost")
    .mountService(authHttpService, "/")

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = server.serve

}
