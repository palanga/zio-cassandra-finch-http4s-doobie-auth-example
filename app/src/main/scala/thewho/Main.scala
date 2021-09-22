package thewho

import thewho.database.cassandra.CassandraAuthDatabase
import thewho.database.module.AuthDatabase
import thewho.types.AppEnv
import zio.Schedule.spaced
import zio._
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.duration._

import scala.language.postfixOps

//object FinchApp extends server.finch.Server {
//
//  val dependencies: ZLayer[Any, Throwable, AuthDatabase] =
//    Console.live ++ Clock.live >>> thewho.database.inmemory.InMemoryAuthDatabase.make
////      CassandraAuthDatabase.dummy
////        .tapError(_ => putStrLn("Retrying in one second..."))
////        .retry(spaced(1 second))
//
//  override implicit val runtime: Runtime[AppEnv] = Runtime.unsafeFromLayer(dependencies ++ ZEnv.live)
//
//  start()
//
//}

object Http4sApp extends server.http4s.Server {
  override val dependencies: ZLayer[Any, Throwable, AuthDatabase] = thewho.database.inmemory.InMemoryAuthDatabase.make
}
