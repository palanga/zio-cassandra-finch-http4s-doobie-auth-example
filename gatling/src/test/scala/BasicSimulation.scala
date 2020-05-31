import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol =
    http
      .baseUrl("http://localhost:8080")

  val scn =
    scenario("BasicSimulation")
      .exec(
        http("signup")
          .post("/signup")
          .body(StringBody(session => s"""{ "id": "${session.startDate}${session.userId}", "secret": "Dalí" }"""))
          .asJson
      )
      .pause(3)
      .exec(
        http("signout")
          .post("/signout")
          .body(StringBody(session => s"""{ "id": "${session.startDate}${session.userId}", "secret": "Dalí" }"""))
          .asJson
      )

  setUp(
    scn.inject(heavisideUsers(5000) during (10 seconds))
  ).protocols(httpProtocol)

}
