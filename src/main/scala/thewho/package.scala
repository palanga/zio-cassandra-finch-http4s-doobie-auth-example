import cats.effect.IO
import scalaz.syntax.id._
import scalaz.zio.TaskR
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import thewho.Main.runtime
import thewho.auth.Auth
import thewho.repository.Repository

package object thewho {

  trait TestEnv  extends Repository.Test with Auth.Test with Console.Live with Clock.Live
  object TestEnv extends TestEnv

  def zioToCatsIO[A](zio: TaskR[TestEnv, A]) = (zio provide TestEnv) |> runtime.unsafeRun |> (IO(_))

}
