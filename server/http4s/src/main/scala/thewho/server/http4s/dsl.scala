package thewho.server.http4s

import org.http4s.dsl.Http4sDsl
import thewho.types.AppTask

object dsl extends Http4sDsl[AppTask]
