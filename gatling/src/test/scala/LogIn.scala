import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class LogIn extends Simulation {
  setUp(
    scenario("log in")
      .exec(utils.login)
      .inject(heavisideUsers(utils.userCount) during (utils.userCount / 400 seconds))
  ).protocols(utils.httpProtocol)
}
