package thewho.database.cassandra

import com.datastax.oss.driver.api.core.cql.Row
import thewho.error.DatabaseException.DatabaseDefect
import zio.ZIO

object codec {
  type Decoder[T] = Row => T
  def decode[T](row: Row)(implicit decode: Decoder[T]) = ZIO effect decode(row) mapError DatabaseDefect
}
