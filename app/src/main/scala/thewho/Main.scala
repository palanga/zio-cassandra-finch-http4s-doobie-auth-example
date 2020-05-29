package thewho

import thewho.database.inmemory.InMemoryAuthDatabase
import thewho.database.module.AuthDatabase
import thewho.server.finch.Server
import thewho.types.AppEnv
import zio._

object Main extends Server {
  val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
  override implicit val runtime: Runtime[AppEnv]         = Runtime.unsafeFromLayer(dependencies ++ ZEnv.live)
  start()
}
