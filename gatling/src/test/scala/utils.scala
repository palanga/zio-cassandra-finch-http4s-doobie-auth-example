import io.gatling.core.Predef._
import io.gatling.http.Predef._

object utils {

  val userCount = 10000

  val httpProtocol = http.baseUrl("http://localhost:8080")

  val signup =
    http("signup")
      .post("/signup")
      .body(StringBody(session => s"""{ "id": "${session.userId}", "secret": "Dalí" }"""))
      .asJson

  val login =
    http("login")
      .post("/login")
      .body(StringBody(session => s"""{ "id": "${session.userId}", "secret": "Dalí" }"""))
      .asJson

  val signout =
    http("signout")
      .post("/signout")
      .body(StringBody(session => s"""{ "id": "${session.userId}", "secret": "Dalí" }"""))
      .asJson

}
