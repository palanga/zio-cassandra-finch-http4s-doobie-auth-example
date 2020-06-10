package thewho.database.cassandra.adapter

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._
import thewho.database.cassandra.adapter.CassandraException._
import thewho.database.cassandra.adapter.ZCqlSession.decode
import zio.stream.{ Stream, ZStream }
import zio.{ Chunk, IO, Ref, ZIO }

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

  def execute(s: ZStatement[_]): IO[CassandraException, AsyncResultSet] =
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
   * Use some of the other alternatives for automatically preparing and caching statements.
   */
  def executeSimple(s: SimpleStatement): IO[QueryExecutionException, ResultSet] =
    ZIO effect (self execute s) mapError QueryExecutionException(s)

  /**
   * The same as [[executeSimple]] but in parallel.
   */
  def executeSimplePar(ss: SimpleStatement*): IO[QueryExecutionException, List[ResultSet]] =
    ZIO collectAllPar (ss map executeSimple)

  /**
   * This version of the datastax driver doesn't support reactive streams but the version that does is incompatible
   * with the last version of finch.
   *
   * @return A stream of chunks, every chunk representing a page.
   */
  def stream[Out](s: ZStatement[Out]): Stream[CassandraException, Chunk[Out]] = {

    import scala.jdk.CollectionConverters._

    def paginate(initial: AsyncResultSet) =
      ZStream.paginateM(initial) { current: AsyncResultSet =>
        if (!current.hasMorePages) ZIO succeed (current -> None)
        else ZIO fromCompletionStage current.fetchNextPage() map { next: AsyncResultSet => current -> Some(next) }
      } mapError QueryExecutionException(s.statement)

    ZStream
      .fromEffect(execute(s))
      .flatMap(paginate)
      .mapM(Chunk fromIterable _.currentPage().asScala mapM decode(s) mapError DecodeException(s))

  }

  private def executePrepared(s: ZStatement[_])(ps: PreparedStatement): IO[QueryExecutionException, AsyncResultSet] =
    ZIO fromCompletionStage self.executeAsync(s bind ps) mapError QueryExecutionException(s.statement)

  private def prepare(s: ZStatement[_]): IO[PrepareStatementException, PreparedStatement] =
    ZIO
      .fromCompletionStage(self prepareAsync s.statement) // TODO logging
      .mapError(PrepareStatementException(s.statement))
      .tap(ps => preparedStatements.update(_ + (s.statement -> ps)))

}
