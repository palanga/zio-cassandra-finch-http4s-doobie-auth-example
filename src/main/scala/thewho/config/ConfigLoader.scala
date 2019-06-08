package thewho.config

import java.net.URLDecoder
import java.nio.file.{ Path, Paths }

import pureconfig.ConfigReader
import pureconfig.generic.auto._
import pureconfig.module.yaml._
import scalaz.zio.{ Task, ZIO }

object ConfigLoader {

  final val loadYamlConfig: Task[Config] = resourcePath("/conf.yaml") >>= loadYamlFromPath[Config]

  private final def resourcePath(path: String): Task[Path] =
    ZIO
      .effect(this.getClass.getResource(path).getFile)
      .map(URLDecoder.decode(_, "UTF-8"))
      .map(Paths.get(_))

  private final def loadYamlFromPath[T](path: Path)(implicit cr: ConfigReader[T]): Task[T] =
    ZIO
      .fromEither(loadYaml[T](path))
      .mapError(configReaderFailures => new Exception(configReaderFailures.toString))

}
