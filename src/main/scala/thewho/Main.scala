package thewho

import org.http4s.server.blaze.BlazeServerBuilder
import scalaz.zio.interop.catz._
import scalaz.zio.interop.catz.implicits._
import scalaz.zio.{ App, DefaultRuntime, Task }

object Main extends App {

  implicit val runtime = new DefaultRuntime {}

  // TODO #13 make host parametric and maybe move to the http package
  val app =
    BlazeServerBuilder[Task]
      .bindHttp(8080, "localhost")
      .withHttpApp(http.app)
      .resource
      .use(_ => Task.never)

  override def run(args: List[String]) = app.orDie

}
