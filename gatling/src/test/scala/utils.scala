import io.gatling.core.Predef._
import io.gatling.http.Predef._

object utils {

  val userCount = 1000

  val httpProtocol = http.baseUrl("http://localhost:8080")

  def signup(id: Int) =
    http("signup")
      .post("/signup")
      .body(StringBody(session => s"""{ "id": "$id-${session.userId}", "secret": "Dalí" }"""))
      .asJson

  def login(id: Int) =
    http("login")
      .post("/login")
      .body(StringBody(session => s"""{ "id": "$id-${session.userId}", "secret": "Dalí" }"""))
      .asJson

  def signout(id: Int) =
    http("signout")
      .post("/signout")
      .body(StringBody(session => s"""{ "id": "$id-${session.userId}", "secret": "Dalí" }"""))
      .asJson

}
