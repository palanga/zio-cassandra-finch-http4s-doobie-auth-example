import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class SignUp extends Simulation {
  setUp(
    scenario("sign up")
      .exec(utils.signup)
      .inject(constantUsersPerSec(300) during (utils.userCount / 300 second))
  ).protocols(utils.httpProtocol)
}
