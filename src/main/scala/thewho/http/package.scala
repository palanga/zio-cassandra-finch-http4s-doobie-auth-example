package thewho

import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import scalaz.zio.Task
import scalaz.zio.interop.catz._
import thewho.http.AuthRouter.authRouter

package object http {

  object syntax {
    object zio extends Http4sDsl[Task]
  }

  val app = authRouter.orNotFound

}
