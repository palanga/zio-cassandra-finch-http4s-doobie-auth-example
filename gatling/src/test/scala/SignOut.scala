import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class SignOut extends Simulation {
  setUp(
    scenario("sign out")
      .exec(utils.signout)
      .inject(constantUsersPerSec(300) during (utils.userCount / 300 second))
  ).protocols(utils.httpProtocol)
}
