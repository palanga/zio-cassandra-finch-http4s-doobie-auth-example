package thewho.database.cassandra

import thewho.database.{ module => db }
import thewho.model.{ Credential, User }
import zio.console.Console
import zio.test.Assertion._
import zio.test._

object CassandraAuthDatabaseTest extends DefaultRunnableSpec {

  val testSuite =
    suite("cassandra")(
      testM("find user") {
        val monet = User(1, Credential("Claude", "Monet"))
        db findUser "Claude" map (assert(_)(equalTo(monet)))
      }
    )

  val dependencies = Console.live >>> CassandraAuthDatabase.dummy

  override def spec = testSuite.provideSomeLayerShared[ZTestEnv](dependencies mapError TestFailure.fail)

}
