package thewho

import thewho.database.inmemory.InMemoryAuthDatabase
import thewho.database.module.AuthDatabase
import thewho.types.AppEnv
import zio._

object FinchApp extends server.finch.Server {
  val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
  override implicit val runtime: Runtime[AppEnv]         = Runtime.unsafeFromLayer(dependencies ++ ZEnv.live)
  start()
}

//object Http4sApp extends server.http4s.Server {
//  override val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
//}
