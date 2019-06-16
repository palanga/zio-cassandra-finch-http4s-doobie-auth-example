package thewho.auth

import scalaz.zio.IO
import thewho.error.Forbidden

case class User(id: UserId, credential: Credential) {

  def validate(thatCredential: Credential): IO[Forbidden.type, UserId] =
    if (credential == thatCredential) IO succeed id
    else IO fail Forbidden

}
