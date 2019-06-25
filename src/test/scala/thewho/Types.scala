package thewho

import scalaz.zio.ZIO
import thewho.error.AppException

object Types {

  case class TestCase[-R, A](description: Description, spec: Spec[R, A])

  type Description = String

  case class Spec[-R, A](expected: ZIO[R, AppException, A], actual: ZIO[R, AppException, A])

}
