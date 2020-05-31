package thewho.server.finch

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch.circe._
import io.finch.{ Endpoint, _ }
import thewho.auth.Authenticator
import thewho.model._
import thewho.types.AppEnv
import zio.interop.catz._
import zio.{ Runtime, Task }

/**
 *
 * Usage:
 *
 * object Main extends Server {
 *   val dependencies: ZLayer[Any, Throwable, AuthDatabase] = InMemoryAuthDatabase.make
 *   override implicit val runtime: Runtime[AppEnv] = Runtime.unsafeFromLayer(dependencies ++ ZEnv.live)
 *   start()
 * }
 *
 */
trait Server extends Endpoint.Module[Task] with App {

  val auth = Authenticator.make

  implicit val runtime: Runtime[AppEnv]

  private val signup: Endpoint[Task, TokenResponse] =
    post("signup" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      Created(runtime.unsafeRun(auth.signup(cred).map(TokenResponse)))
    }

  private val login: Endpoint[Task, TokenResponse] =
    post("login" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      Ok(runtime.unsafeRun(auth.login(cred).map(TokenResponse)))
    }

  private val changePassword: Endpoint[Task, TokenResponse] =
    post("change-password" :: jsonBody[CredentialSecretUpdateRequest]) { body: CredentialSecretUpdateRequest =>
      Ok(runtime.unsafeRun(auth.changePassword(body.oldCredential, body.newSecret).map(TokenResponse)))
    }

  private val signout: Endpoint[Task, String] =
    post("signout" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      runtime.unsafeRun(auth.signout(cred))
      if (true) NoContent else Ok("hack")
    }

  private val findCredentialId: Endpoint[Task, UserCredentialIdResponse] =
    post("find-credential-id" :: jsonBody[UserCredentialIdRequest]) { body: UserCredentialIdRequest =>
      Ok(runtime.unsafeRun(auth.findCredentialId(body.token).map(UserCredentialIdResponse)))
    }

  private val endpoints = (
    signup
      :+: login
      :+: signout
      :+: changePassword
      :+: findCredentialId
  ) handle errorHandler

  private def errorHandler[T]: PartialFunction[Throwable, Output[T]] = {
    case e: Exception => BadRequest(e)
    case _            => BadRequest(new Exception())
  }

  def start() = Await.ready(Http.server.serve(":8080", endpoints.toService))

}
