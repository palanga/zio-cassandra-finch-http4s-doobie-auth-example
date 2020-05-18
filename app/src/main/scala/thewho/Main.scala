package thewho

import thewho.database.inmemory.InMemoryAuthDatabase
import thewho.database.module.AuthDatabase
import thewho.server.http4s.Http4sBlazeServer
import zio._

object Main extends App {

  val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
  //  val dependencies: ZLayer[Any, Throwable, AuthDatabase] = zio.blocking.Blocking.live >>> DoobieAuthDatabase(???)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    Http4sBlazeServer.start.as(ExitCode.success).provideSomeLayer[ZEnv](dependencies).orDie

}
