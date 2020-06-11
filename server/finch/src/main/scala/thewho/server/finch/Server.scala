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
import zio.{ Runtime, Task, ZIO }

import scala.concurrent.Future

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

  implicit val runtime: Runtime[AppEnv]

  def start() = Await.ready(Http.server.serve(":8080", endpoints.toService))

  private val auth = Authenticator.make

  private val signup: Endpoint[Task, TokenResponse] =
    post("signup" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      execute(auth.signup(cred) map TokenResponse map Created)
    }

  private val login: Endpoint[Task, TokenResponse] =
    post("login" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      execute(auth.login(cred) map TokenResponse map Ok)
    }

  private val changePassword: Endpoint[Task, TokenResponse] =
    post("change-password" :: jsonBody[CredentialSecretUpdateRequest]) { body: CredentialSecretUpdateRequest =>
      execute(auth.changePassword(body.oldCredential, body.newSecret) map TokenResponse map Ok)
    }

  private val signout: Endpoint[Task, String] =
    post("signout" :: jsonBody[UnvalidatedCredential]) { cred: UnvalidatedCredential =>
      execute(auth.signout(cred) as NoContent[String])
    }

  private val findCredentialId: Endpoint[Task, UserCredentialIdResponse] =
    post("find-credential-id" :: jsonBody[UserCredentialIdRequest]) { body: UserCredentialIdRequest =>
      execute(auth.findCredentialId(body.token) map UserCredentialIdResponse map Ok)
    }

  private val endpoints = (
    signup
      :+: login
      :+: signout
      :+: changePassword
      :+: findCredentialId
  ) handle errorHandler

  private def execute[E <: Throwable, A](zio: ZIO[AppEnv, E, A]): Future[A] = runtime.unsafeRunToFuture(zio).future

  private def errorHandler[T]: PartialFunction[Throwable, Output[T]] = {
    case e: Exception => BadRequest(e)
    case _            => BadRequest(new Exception())
  }

}
