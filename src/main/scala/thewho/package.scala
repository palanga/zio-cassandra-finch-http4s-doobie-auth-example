import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import thewho.auth.Auth
import thewho.repository.Repository

package object thewho {

  trait TestEnv  extends Repository.Live with Auth.Test with Console.Live with Clock.Live
  object TestEnv extends TestEnv

}
