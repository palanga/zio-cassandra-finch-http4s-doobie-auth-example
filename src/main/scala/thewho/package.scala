import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import thewho.auth.Auth
import thewho.repository.Repository

package object thewho {

  trait TestEnv extends Repository.Test with Auth.Test with Console.Live with Clock.Live
  object TestEnv extends TestEnv

  def printStackTraceAndFail(t: Throwable): Int = {
    t printStackTrace()
    1
  }

  def printResultAndSuccess(r: Any): Int = {
    println(r)
    0
  }

}
