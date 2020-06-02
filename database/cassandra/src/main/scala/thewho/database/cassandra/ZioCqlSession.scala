package thewho.database.cassandra

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{ ResultSet, Row, Statement }
import zio.interop.reactivestreams.publisherToStream
import zio.stream.{ Stream, ZStream }
import zio.{ Task, ZIO }

import scala.language.postfixOps

class ZioCqlSession(val self: CqlSession) extends AnyVal {

  def close: Task[Unit] = ZIO effect self.close()

  def execute(s: Statement[_]): Task[ResultSet] =
    ZIO effect (self execute s)

  def executeHead(s: Statement[_]): Task[Option[Row]] =
    execute(s) map (result => Option(result.one()))

  def stream(s: Statement[_]): Stream[Throwable, ReactiveRow] =
    self.executeReactive(s).toStream(128)

  def list(s: Statement[_]): Task[List[Row]] =
    ZStream fromJavaIteratorEffect (execute(s) map (_.iterator())) runCollect

}
