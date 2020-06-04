package thewho.database.cassandra

import thewho.database.{ module => db }
import thewho.model.{ Credential, UnvalidatedCredential }
import utils.zio.test.syntax.zioops.ZIOOps
import zio.Schedule.spaced
import zio.clock.Clock
import zio.console.{ putStrLn, Console }
import zio.duration._
import zio.test._

import scala.language.postfixOps

object CassandraAuthDatabaseTest extends DefaultRunnableSpec {

  val testSuite =
    suite("cassandra")(
      testM("create user") {
        val frida = UnvalidatedCredential("Frida", "Kahlo")
        (db createUser frida) map (_.credential) assertEqualTo Credential(frida.id, frida.secret)
      },
      testM("create and find user") {
        val miroInput = UnvalidatedCredential("Joan", "Miró")
        val expected  = Credential(miroInput.id, miroInput.secret)
        (db createUser miroInput) *> (db findUser miroInput.id) map (_.credential) assertEqualTo expected
      },
      testM("create, find and delete nik because we don't want him in our db") {
        val nik = UnvalidatedCredential("nik", "chorro")
        (db createUser nik) *> (db findUser nik.id) flatMap (db deleteUser _.id) as assertCompletes
      },
    )

  val dependencies =
    Console.live ++ Clock.live >>> CassandraAuthDatabase.dummy
      .tapError(_ => putStrLn("Retrying in one second..."))
      .retry(spaced(1 second))

  override def spec = testSuite.provideSomeLayerShared[ZTestEnv](dependencies mapError TestFailure.fail)

}
