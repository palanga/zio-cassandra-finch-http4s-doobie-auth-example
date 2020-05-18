package thewho

object model {

  type Token            = String
  type CredentialId     = String
  type CredentialSecret = String
  type UserId           = Int // TODO uuid
  type PublicKey        = String
  type PrivateKey       = String
  type Seconds          = Long
  type Timestamp        = Long

  // TODO hash secret on db
  // TODO type filed (like email, username, phone number, facebook, etc)
  // TODO verified field
  case class Credential(id: CredentialId, secret: CredentialSecret)

  case class UnvalidatedCredential(id: CredentialId, secret: CredentialSecret) {
    def isSame(other: Credential): Boolean = other == Credential(id, secret)
  }

  case class TokenContent private (id: UserId, exp: Timestamp)
  object TokenContent {
    def fromIssued(issued: Timestamp, ttl: Seconds, id: UserId): TokenContent = TokenContent(id, issued + ttl)
    def fromExpiration(expiration: Timestamp, id: UserId): TokenContent       = TokenContent(id, expiration)
  }

  // TODO support several credentials for every user
  case class User(id: UserId, credential: Credential)

  // TODO refresh token
  // TODO move to server module
  case class TokenResponse(token: Token)

  // TODO move to server module
  case class CredentialSecretUpdateRequest(oldCredential: UnvalidatedCredential, newSecret: CredentialSecret)

  // TODO move to server module
  case class UserCredentialIdRequest(token: Token)

  // TODO move to server module
  case class UserCredentialIdResponse(credentialId: CredentialId)

}
