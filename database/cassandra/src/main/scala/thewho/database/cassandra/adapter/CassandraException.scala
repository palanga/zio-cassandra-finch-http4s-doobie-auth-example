package thewho.database.cassandra.adapter

import com.datastax.oss.driver.api.core.cql.SimpleStatement

sealed trait CassandraException extends Exception with Product with Serializable
object CassandraException {

  case class DecodeException(s: ZStatement[_])(cause: Throwable)
      extends Exception(s"Failed decoding the result of <<${s.statement.getQuery}>>: ${cause.getMessage}")
      with CassandraException

  case class EmptyResultSetException(s: ZStatement[_])
      extends Exception(s"${s.statement.getQuery} returned empty")
      with CassandraException

  case class PrepareStatementException(s: SimpleStatement)(cause: Throwable)
      extends Exception(s"Failed preparing the statement <<${s.getQuery}>>: ${cause.getMessage}")
      with CassandraException

  case class QueryExecutionException(s: SimpleStatement)(cause: Throwable)
      extends Exception(s"Failed executing <<${s.getQuery}>>: ${cause.getMessage}")
      with CassandraException

  case class SessionCloseException(cause: Throwable)
      extends Exception(s"Failed closing cql session: ${cause.getMessage}")
      with CassandraException

  case class SessionOpenException(cause: Throwable)
      extends Exception(s"Failed opening cql session: ${cause.getMessage}")
      with CassandraException

}
