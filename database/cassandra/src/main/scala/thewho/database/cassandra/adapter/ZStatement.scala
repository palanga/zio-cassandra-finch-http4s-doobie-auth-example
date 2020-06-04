package thewho.database.cassandra.adapter

import com.datastax.oss.driver.api.core.cql.{ BoundStatement, PreparedStatement, Row, SimpleStatement }

object ZStatement {

  def apply(query: String): ZSimpleStatement[Row] =
    new ZSimpleStatement[Row](SimpleStatement.builder(query).build(), _.bind(), identity)

  implicit class StringOps(val self: String) extends AnyVal {
    def statement: ZSimpleStatement[Row] = apply(self)
  }

  implicit class SimpleStatementOps(val self: SimpleStatement) extends AnyVal {
    def bind(params: Any*): ZBoundStatement[Row]    = new ZBoundStatement[Row](self, _.bind(params: _*), identity)
    def decode[T](f: Row => T): ZSimpleStatement[T] = new ZSimpleStatement[T](self, _.bind(), f)
  }

}

trait ZStatement[Out] {
  private[adapter] val statement: SimpleStatement
  private[adapter] val bind: PreparedStatement => BoundStatement
  private[adapter] val decodeUnsafe: Row => Out
}

final class ZSimpleStatement[Out](
  private[adapter] val statement: SimpleStatement,
  private[adapter] val bind: PreparedStatement => BoundStatement,
  private[adapter] val decodeUnsafe: Row => Out,
) extends ZStatement[Out] {
  def bind(params: Any*): ZBoundStatement[Out]    = new ZBoundStatement[Out](statement, _.bind(params: _*), decodeUnsafe)
  def decode[T](f: Row => T): ZSimpleStatement[T] = new ZSimpleStatement[T](statement, bind, f)
}

final class ZBoundStatement[Out](
  private[adapter] val statement: SimpleStatement,
  private[adapter] val bind: PreparedStatement => BoundStatement,
  private[adapter] val decodeUnsafe: Row => Out,
) extends ZStatement[Out] {
  def decode[T](f: Row => T): ZBoundStatement[T] = new ZBoundStatement[T](statement, bind, f)
}
