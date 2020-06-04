package thewho.database.cassandra

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._
import zio.interop.reactivestreams.publisherToStream
import zio.stream.{ Stream, ZStream }
import zio.{ Task, ZIO }

import scala.language.postfixOps

object ZCqlSession {
  def apply(self: => CqlSession): Task[ZCqlSession] = ZIO effect new ZCqlSession(self)
}

class ZCqlSession(val self: CqlSession) extends AnyVal {

  def close: Task[Unit] =
    ZIO effect self.close()

  def execute(s: Statement[_]): Task[ResultSet] =
    ZIO effect (self execute s)

  def executePar(s: Statement[_]*): Task[List[ResultSet]] =
    ZIO collectAllPar (s map execute)

  def executeHead(s: Statement[_]): Task[Option[Row]] =
    execute(s) map (result => Option(result.one()))

  def list(s: Statement[_]): Task[List[Row]] =
    ZStream fromJavaIteratorEffect (execute(s) map (_.iterator())) runCollect

  def prepare(s: SimpleStatement): Task[PreparedStatement] =
    ZIO effect (self prepare s)

  def preparePar(s: SimpleStatement*): Task[List[PreparedStatement]] =
    ZIO collectAllPar (s map prepare)

  def stream(s: Statement[_]): Stream[Throwable, ReactiveRow] =
    self executeReactive s toStream 128

}
