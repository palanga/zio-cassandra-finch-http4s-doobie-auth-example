package thewho

import thewho.database.module.AuthDatabase
import zio.{ RIO, ZEnv }

object types {
  type AppEnv     = ZEnv with AuthDatabase
  type AppTask[A] = RIO[AppEnv, A]
}
