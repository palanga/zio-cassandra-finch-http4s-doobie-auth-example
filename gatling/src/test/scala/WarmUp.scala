import io.gatling.core.Predef._

class WarmUp extends Simulation {
  setUp(
    scenario("warm up")
      .exec(utils.signup(-1))
      .exec(utils.login(-1))
      .exec(utils.signout(-1))
      .inject(atOnceUsers(10))
  ).protocols(utils.httpProtocol)
}
