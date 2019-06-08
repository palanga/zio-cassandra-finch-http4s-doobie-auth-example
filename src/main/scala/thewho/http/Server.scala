package thewho.http

import org.http4s.server.blaze.BlazeServerBuilder
import scalaz.zio.ZIO
import scalaz.zio.interop.catz._
import thewho.config.ServerConfig
import thewho.{ http, AppEnvironment, AppTask }

object Server {

  def fromConfig(config: ServerConfig): ZIO[AppEnvironment, Throwable, Unit] =
    ZIO.runtime[AppEnvironment] >>= { implicit env =>
      BlazeServerBuilder[AppTask]
        .bindHttp(config.port, config.host)
        .withHttpApp(http.Routes.all)
        .serve
        .compile
        .drain

    }

}
