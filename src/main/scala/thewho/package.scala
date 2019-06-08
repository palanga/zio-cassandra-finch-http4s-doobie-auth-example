import scalaz.zio.TaskR
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import thewho.auth.Auth
import thewho.repository.Repository

package object thewho {

  type AppEnvironment = Clock with Console with Blocking with Repository with Auth
  type AppTask[A]     = TaskR[AppEnvironment, A]

}
