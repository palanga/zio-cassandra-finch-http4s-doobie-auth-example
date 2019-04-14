package thewho

import scalaz.zio.ZIO
import thewho.auth.AuthId


package object repository extends Repository.Service[Repository] {

  override def findAuthSecret(authId: AuthId) =
    ZIO accessM (_.repository findAuthSecret authId)

  override def findCredentialId(authId: AuthId) =
    ZIO accessM (_.repository findCredentialId authId)

  override def findCredential(authId: AuthId) =
    ZIO accessM (_.repository findCredential authId)

}
