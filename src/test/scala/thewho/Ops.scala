package thewho

import org.scalatest.{ FreeSpec, Matchers }
import scalaz.zio.ZIO
import thewho.Types.TestCase
import thewho.error.HasCause

/**
 * Example:
 *
 * ```
 * class TestSuite extends Ops {
 *
 *   object runtime extends TestRuntime
 *
 *   val cases: List[ TestCase[R, _] ] = ???
 *
 *   runtime.unsafeRun(ZIO.foreach(cases)(compareResults[R]).provide(TestRuntime))
 *
 * }
 * ```
 *
 */
trait Ops extends FreeSpec with Matchers {

  /**
   * Given a [[TestCase]] convert it to a ScalaTest test case, comparing
   * the expected value (failed or succeeded) with the actual one.
   *
   * @param testCase The test case
   * @tparam R The type of the required modules to run the ZIO
   */
  def compareResults[R](testCase: TestCase[R, _]): ZIO[R, Nothing, Unit] =
    for {
      expected <- testCase.spec.expected.mapError(unboxHasCause).catchAll(ZIO succeed)
      actual   <- testCase.spec.actual.mapError(unboxHasCause).catchAll(ZIO succeed)
    } yield testCase.description in { actual shouldBe expected }

  /**
   * If the error was a decorated AppException that has a cause, return the cause so we
   * have more information in the logs about what happened.
   *
   * @param t The raw error
   */
  def unboxHasCause(t: Throwable): Throwable = t match {
    case hasCause: HasCause => hasCause.cause
    case _                  => t
  }

}
