import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class SignOut extends Simulation {
  setUp(
    scenario("sign out")
      .exec(utils.signout(0))
      .inject(constantUsersPerSec(370) during (utils.userCount / 370 seconds))
  ).protocols(utils.httpProtocol)
}
