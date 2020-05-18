package thewho.auth

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import zio.test.environment.{ testEnvironment, TestEnvironment }
import zio.test.{ defaultTestRunner, Spec, TestSuccess }

/**
 * The only purpose of this class is to have out of the box code coverage on intelliJ
 */
class CoverageTest extends AnyFreeSpec with Matchers {

  def render(spec: Spec[TestEnvironment, Any, TestSuccess]): Unit =
    spec.caseValue match {
      case Spec.SuiteCase(label, ztests, _) =>
        label - {
          defaultTestRunner.runtime.unsafeRun(ztests.provideLayer(testEnvironment).useNow).foreach(render)
        }
      case Spec.TestCase(label, ztest, _)   =>
        label in {
          defaultTestRunner.runtime.unsafeRun(ztest.provideLayer(testEnvironment))
        }
    }

  render(AuthenticatorTest.spec)

}
