import io.gatling.core.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class WarmUp extends Simulation {
  setUp(
    scenario("warm up")
      .exec(utils.signup)
      .exec(utils.login)
      .exec(utils.signout)
      .inject(heavisideUsers(10) during (1 second))
  ).protocols(utils.httpProtocol)
}
