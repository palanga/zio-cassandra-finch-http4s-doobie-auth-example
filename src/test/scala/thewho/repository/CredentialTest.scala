package thewho.repository

import scalaz.zio.ZIO
import thewho.Types.{ Spec, TestCase }
import thewho.auth.Credential
import thewho.error.{ CredentialNotFound, UserNotFound }

object CredentialTest {

  private val createUserAndFindCredential: Spec[Repository, Credential] =
    Spec(
      ZIO succeed Credential("Rene", "Magritte"),
      createUser(Credential("Rene", "Magritte")).map(_.id) >>= findCredential
    )

  private val findCredentialForNonExistentUser: Spec[Repository, Credential] =
    Spec(
      ZIO fail UserNotFound,
      findCredential(0)
    )

  private val createUserAndUpdateItsCredential =
    Spec(
      ZIO succeed Credential("Salvador", "Dalí"),
      createUser(Credential("Salvador", "Allende")) *> updateCredential(Credential("Salvador", "Dalí"))
    )

  private val updateNonExistentCredential =
    Spec(
      ZIO fail CredentialNotFound,
      updateCredential(Credential("non existent", "doesn't matter"))
    )

  val cases: List[TestCase[Repository, _]] =
    List(
      "Create a user and find its credential should succeed with the credential"     -> createUserAndFindCredential,
      "Find a credential by userId that doesn't exist should fail with UserNotFound" -> findCredentialForNonExistentUser,
      "Create a user and update its credential should succeed with the credential"   -> createUserAndUpdateItsCredential,
      "Update non existent credential should fail with CredentialNotFound"           -> updateNonExistentCredential
    ) map { case (desc, spec) => TestCase(desc, spec) }

}
