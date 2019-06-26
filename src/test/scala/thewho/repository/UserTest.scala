package thewho.repository

import scalaz.zio.ZIO
import thewho.Types.{ Spec, TestCase }
import thewho.auth.{ Credential, User }
import thewho.error.{ CredentialAlreadyExist, CredentialNotFound, UserNotFound }

object UserTest {

  private val createSingleUser: Spec[Repository, Credential] =
    Spec(
      ZIO succeed Credential("Claude", "Monet"),
      createUser(Credential("Claude", "Monet")) map (_.credential)
    )

  private val createAndFindUserByUserId: Spec[Repository, Credential] =
    Spec(
      ZIO succeed Credential("Joan", "Miró"),
      (createUser(Credential("Joan", "Miró")).map(_.id) >>= findUser) map (_.credential)
    )

  private val createAndFindUserByCredentialId: Spec[Repository, Credential] =
    Spec(
      ZIO succeed Credential("Frida", "Kahlo"),
      createUser(Credential("Frida", "Kahlo")) *> findUser("Frida") map (_.credential)
    )

  private val createTheSameUserTwice: Spec[Repository, User] =
    Spec(
      ZIO fail CredentialAlreadyExist,
      createUser(Credential("Dorothea", "Tanning")) *> createUser(Credential("Dorothea", "Tanning"))
    )

  private val createAndDeleteUser: Spec[Repository, Unit] =
    Spec(
      ZIO.succeed(()),
      (createUser(Credential("Pablo", "Picasso")).map(_.id) >>= deleteUser) map (_ => ())
    )

  private val findUserThatDoesNotExistsByUserId: Spec[Repository, User] =
    Spec(
      ZIO fail UserNotFound,
      findUser(0)
    )

  private val findUserThatDoesNotExistsByCredentialId: Spec[Repository, User] =
    Spec(
      ZIO fail CredentialNotFound,
      findUser("non existent credential id")
    )

  val cases: List[TestCase[Repository, _]] =
    List(
      "Create a single user should succeed with the credential"                            -> createSingleUser,
      "Create and find a user by userId should succeed with the credential"                -> createAndFindUserByUserId,
      "Create and find a user by credentialId should succeed with the credential"          -> createAndFindUserByCredentialId,
      "Create the same user twice should fail with a CredentialAlreadyExist error"         -> createTheSameUserTwice,
      "Create and delete a user should succeed with Unit"                                  -> createAndDeleteUser,
      "Find a user by userId that doesn't exist should fail with UserNotFound"             -> findUserThatDoesNotExistsByUserId,
      "Find a user by credentialId that doesn't exist should fail with CredentialNotFound" -> findUserThatDoesNotExistsByCredentialId
    ) map { case (desc, spec) => TestCase(desc, spec) }

}
