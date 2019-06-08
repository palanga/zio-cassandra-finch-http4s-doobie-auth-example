package thewho

import doobie.hikari.HikariTransactor
import scalaz.zio._
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.scheduler.Scheduler
import thewho.auth.TestAuth
import thewho.config.ConfigLoader
import thewho.http.Server
import thewho.repository.{ DoobieRepository, Transactor }

object Main extends App {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    (for {

      config      <- ConfigLoader.loadYamlConfig
      server      = Server.fromConfig(config.server)
      transactorR = Transactor.fromConfig(config.db)

      program <- transactorR.use { transactor =>
                  server.provideSome[Environment] { base =>
                    new Clock with Console with Blocking with DoobieRepository with TestAuth {

                      override protected def xa: HikariTransactor[Task] = transactor

                      override val scheduler: Scheduler.Service[Any] = base.scheduler
                      override val console: Console.Service[Any]     = base.console
                      override val clock: Clock.Service[Any]         = base.clock
                      override val blocking: Blocking.Service[Any]   = base.blocking

                    }
                  }
                }

    } yield program).orDie.map(_ => 0)

}
