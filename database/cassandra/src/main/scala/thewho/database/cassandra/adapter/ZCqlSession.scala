package thewho.database.cassandra.adapter

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._
import thewho.database.cassandra.adapter.CassandraException._
import thewho.database.cassandra.adapter.ZCqlSession.decode
import zio.stream.{ Stream, ZStream }
import zio.{ IO, Ref, ZIO }

object ZCqlSession {

  def apply(self: => CqlSession): IO[SessionOpenException, ZCqlSession] =
    Ref
      .make(Map.empty[SimpleStatement, PreparedStatement])
      .flatMap(ZIO effect new ZCqlSession(self, _))
      .mapError(SessionOpenException)

  private def decode[T](s: ZStatement[T])(row: Row): IO[DecodeException, T] =
    ZIO effect s.decodeUnsafe(row) mapError (DecodeException(s)(_))

}

final class ZCqlSession private (
  private val self: CqlSession,
  private val preparedStatements: Ref[Map[SimpleStatement, PreparedStatement]],
) {

  def close: IO[SessionCloseException, Unit] =
    ZIO effect self.close() mapError SessionCloseException

  def execute(s: ZStatement[_]): IO[CassandraException, ResultSet] =
    preparedStatements.get.flatMap(_.get(s.statement).fold(prepare(s) flatMap executePrepared(s))(executePrepared(s)))

  def executeHeadOption[Out](s: ZStatement[Out]): IO[CassandraException, Option[Out]] =
    execute(s)
      .map(result => Option(result.one()))
      .flatMap(maybeRow => ZIO effect maybeRow.map(s.decodeUnsafe) mapError DecodeException(s))

  def executeHeadOrFail[Out](s: ZStatement[Out]): IO[CassandraException, Out] =
    execute(s).flatMap(rs =>
      if (rs.one() != null) decode(s)(rs.one())
      else ZIO fail EmptyResultSetException(s)
    )

  /**
   * Execute a simple statement without first calculating and caching its prepared statement.
   * Use some of the other alternatives for automatic preparation and caching.
   */
  def executeSimple(s: SimpleStatement): IO[QueryExecutionException, ResultSet] =
    ZIO effect (self execute s) mapError QueryExecutionException(s)

  /**
   * The same as [[executeSimple]] but in parallel.
   */
  def executeSimplePar(ss: SimpleStatement*): IO[QueryExecutionException, List[ResultSet]] =
    ZIO collectAllPar (ss map executeSimple)

  def list[Out](s: ZStatement[Out]): IO[CassandraException, List[Out]] = stream(s).runCollect

  /**
   * This version of the datastax driver doesn't support reactive streams but the version that does it's incompatible
   * with the last version of finch.
   */
  def stream[Out](s: ZStatement[Out]): Stream[CassandraException, Out] =
    ZStream
      .fromJavaIteratorEffect(execute(s) map (_.iterator()))
      .mapError(QueryExecutionException(s.statement)(_))
      .mapM(decode(s))

  private def executePrepared(s: ZStatement[_])(ps: PreparedStatement): IO[QueryExecutionException, ResultSet] =
    ZIO effect self.execute(s.bind(ps)) mapError QueryExecutionException(s.statement)

  private def prepare(s: ZStatement[_]): IO[PrepareStatementException, PreparedStatement] =
    ZIO
      .effect(self prepare s.statement) // TODO logging
      .mapError(PrepareStatementException(s.statement))
      .tap(ps => preparedStatements.update(_ + (s.statement -> ps)))

}
