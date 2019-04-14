package thewho

import java.util.concurrent.TimeUnit.SECONDS

import scalaz.zio.clock.currentTime
import scalaz.zio.console.{getStrLn, putStrLn}
import scalaz.zio.{App, ZIO}
import thewho.auth.{PhoneAuth, decode, login}

object Main extends App {

  override def run(args: List[String]): ZIO[Any, Nothing, Int] =
    test.provide(TestEnv).fold(printStackTraceAndFail, printResultAndSuccess)

  def test = for {
    _           <- putStrLn("choose phone number")
    phoneNumber <- getStrLn
    _           <- putStrLn("write your password")
    password    <- getStrLn
    currentTime <- currentTime(SECONDS)
    token       <- login(PhoneAuth(phoneNumber, password), currentTime)
    decoded     <- decode(token)
  } yield (decoded, token)

}
