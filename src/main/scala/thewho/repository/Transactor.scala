package thewho.repository

import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import scalaz.zio.interop.catz._
import scalaz.zio.{ Managed, Reservation, Task }
import thewho.config.DBConfig

object Transactor {

  // Resource yielding a transactor configured with a bounded connect EC and an unbounded transaction EC.
  def fromConfig(config: DBConfig): Managed[Throwable, HikariTransactor[Task]] = {
    val xa = for {
      connectEC  <- ExecutionContexts.fixedThreadPool[Task](config.connectionThreadPoolSize)
      transactEC <- ExecutionContexts.cachedThreadPool[Task]
      transactor <- HikariTransactor.newHikariTransactor[Task](
                     config.driverName,
                     config.url,
                     config.username,
                     config.password,
                     connectEC,
                     transactEC
                   )
    } yield transactor

    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(Task.succeed(transactor), cleanupM.orDie)
    }.uninterruptible

    Managed(res)
  }

}
