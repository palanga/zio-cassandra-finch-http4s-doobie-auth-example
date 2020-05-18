package thewho.server.http4s

import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeServerBuilder
import thewho.auth.Authenticator.{ make => auth }
import thewho.model._
import thewho.types.{ AppEnv, AppTask }
import zio.ZIO

object Http4sBlazeServer {

  private val routes: HttpRoutes[AppTask] = {

    import dsl._
    import io.circe.generic.auto._
    import org.http4s.circe.CirceEntityCodec._
    import zio.interop.catz.taskConcurrentInstance

    HttpRoutes.of[AppTask] {
      case req @ POST -> Root / "signup"             =>
        req decode [UnvalidatedCredential](auth.signup(_).map(TokenResponse).foldM(ExceptionHandler.all, Created(_)))
      case req @ (GET | POST) -> Root / "login"      =>
        req decode [UnvalidatedCredential](auth.login(_).map(TokenResponse).foldM(ExceptionHandler.all, Ok(_)))
      case req @ POST -> Root / "change-password"    =>
        req decode [CredentialSecretUpdateRequest] (body =>
          auth.changePassword(body.oldCredential, body.newSecret).map(TokenResponse).foldM(ExceptionHandler.all, Ok(_))
        )
      case req @ POST -> Root / "signout"            =>
        req decode [UnvalidatedCredential](auth.signout(_).foldM(ExceptionHandler.all, _ => NoContent()))
      case req @ POST -> Root / "find-credential-id" =>
        req decode [UserCredentialIdRequest] (body =>
          auth.findCredentialId(body.token).map(UserCredentialIdResponse).foldM(ExceptionHandler.all, Ok(_))
        )
    }

  }

  val start: ZIO[AppEnv, Throwable, Unit] = {
    import org.http4s.syntax.kleisli.http4sKleisliResponseSyntaxOptionT
    import zio.interop.catz.{ taskEffectInstance, zioTimer }
    ZIO
      .runtime[AppEnv]
      .flatMap { implicit runtime =>
        BlazeServerBuilder[AppTask]
          .bindHttp(8080, "localhost")
          .withHttpApp(routes.orNotFound)
          .serve
          .compile
          .drain
      }
  }

}
