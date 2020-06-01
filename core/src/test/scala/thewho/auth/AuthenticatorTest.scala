package thewho.auth

import thewho.auth.Authenticator.{ make => auth }
import thewho.database.inmemory.InMemoryAuthDatabase.{ make => dbLayer }
import thewho.database.module.AuthDatabase
import thewho.error.AuthException.AuthError.{ Forbidden, TokenExpired }
import thewho.error.DatabaseException.DatabaseError.{ CredentialAlreadyExist, CredentialNotFound }
import thewho.model._
import utils.zio.test.syntax.zioops.ZIOOps
import zio.clock.Clock
import zio.test._
import zio.test.environment.TestEnvironment

object AuthenticatorTest extends DefaultRunnableSpec {

  val testSuite =
    suite("auth")(
      testM("signup a user for the first time should succeed") {
        auth signup UnvalidatedCredential("Frida", "Kahlo") as assertCompletes
      },
      testM("signup the same user twice should fail") {
        val miro = UnvalidatedCredential("Joan", "Miró")
        (auth signup miro) *> (auth signup miro) assertFailsWith CredentialAlreadyExist
      },
      testM("login should succeed") {
        val morisot = UnvalidatedCredential("Berthe", "Morisot")
        (auth signup morisot) *> (auth login morisot) as assertCompletes
      },
      testM("login with incorrect password should fail") {
        val dali    = UnvalidatedCredential("Salvador", "Dalí")
        val allende = UnvalidatedCredential("Salvador", "Allende")
        (auth signup dali) *> (auth login allende) assertFailsWith Forbidden
      },
      testM("change password should succeed") {
        val tarsila   = UnvalidatedCredential("Tarsila", "Amaral")
        val newSecret = "do Amaral"
        (auth signup tarsila) *> auth.changePassword(tarsila, newSecret) as assertCompletes
      },
      testM("change password with incorrect credential should fail") {
        val vanGoghGood = UnvalidatedCredential("Vincent", "van Gogh")
        val vanGohgBad  = UnvalidatedCredential("Vincent", "van Gohg")
        val newSecret   = "Willem van Gogh"
        (auth signup vanGoghGood) *> auth.changePassword(vanGohgBad, newSecret) assertFailsWith Forbidden
      },
      testM("signout with incorrect credential should fail") {
        val varo = UnvalidatedCredential("Remedios", "Varo")
        val vara = UnvalidatedCredential("Remedios", "Vara")
        (auth signup varo) *> (auth signout vara) assertFailsWith Forbidden
      },
      testM("decode should succeed") {
        val spec = (auth signup UnvalidatedCredential("Claude", "Monet")) flatMap auth.decode as assertCompletes
        // provide the live clock because the jwt library we use gets the actual system time with side effects:
        spec.provideSomeLayer[AuthDatabase](Clock.live)
      },
      testM("find credential id should return the same credential id we used to signup") {
        val tanning = UnvalidatedCredential("Dorothea", "Tanning")
        val spec    = (auth signup tanning) flatMap auth.findCredentialId assertEqualTo tanning.id
        // provide the live clock because the jwt library we use gets the actual system time with side effects:
        spec.provideSomeLayer[AuthDatabase](Clock.live)
      },
      testM("decode expired token should fail") {
        // This is tricky because auth.decode uses a library which takes the actual current system time and we can't
        // change that, but auth.signup makes use of the zio.clock we provide. Thus, providing no clock (defaults to
        // the test environment one) the token will be issued at timestamp 0, and decode will see it expired.
        auth.signup(UnvalidatedCredential("Pablo", "Picasso")).flatMap(auth.decode).assertFailsWithType[TokenExpired]
      },
      testM("signout nik should succeed because we don't want people like him") {
        val nik = UnvalidatedCredential("nik", "chorro asqueroso")
        (auth signup nik) *> (auth signout nik) as assertCompletes
      },
      testM("login nik should fail because we don't accept thieves, ergo he's not signed up") {
        auth login UnvalidatedCredential("nik", "chorro asqueroso") assertFailsWith CredentialNotFound
      },
      testM("change nik's password should fail because we don't accept thieves, ergo he's not signed up") {
        val nik = UnvalidatedCredential("nik", "chorro asqueroso")
        auth changePassword (nik, "ladron") assertFailsWith CredentialNotFound
      },
      testM("signout nik should fail because we don't accept thieves, ergo he's not signed up") {
        auth signout UnvalidatedCredential("nik", "chorro asqueroso") assertFailsWith CredentialNotFound
      },
    )

  override def spec = testSuite.provideSomeLayerShared[TestEnvironment](dbLayer)

}
