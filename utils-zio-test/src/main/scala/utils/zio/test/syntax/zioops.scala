package utils.zio.test.syntax

import zio.test.Assertion.{ anything, equalTo, fails, isSubtype }
import zio.test.{ assert, assertM, Assertion, TestResult }
import zio.{ URIO, ZIO }

import scala.reflect.ClassTag

object zioops {

  implicit final class ZIOOps[-R, +E, +A](private val self: ZIO[R, E, A]) extends AnyVal {

    def assertFails(assertion: Assertion[Any]): URIO[R, TestResult] = assertM(self.run)(fails(assertion))

    def assertFailsWith(expected: Any): URIO[R, TestResult] = assertM(self.run)(fails(equalTo(expected)))

    def assertFailsWithType[E1](implicit C: ClassTag[E1]): URIO[R, TestResult] =
      assertM(self.run)(fails(isSubtype[E1](anything)))

    def assertEqualTo(expected: Any): ZIO[R, E, TestResult] = self.map(assert(_)(equalTo(expected)))

  }

}
