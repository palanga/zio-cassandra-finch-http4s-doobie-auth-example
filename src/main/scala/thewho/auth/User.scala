package thewho.auth

import scalaz.zio.Task

case class User(id: UserId, credential: Credential) {

  def validate(thatCredential: Credential) =
    if (credential == thatCredential) Task succeed id
    else Task fail new Exception("Couldn't validate credential")

}

object User {

  def from(userId: UserId, credentialId: CredentialId, secret: CredentialSecret) =
    User(userId, Credential(credentialId, secret))

}
