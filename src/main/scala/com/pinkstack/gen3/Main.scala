package com.pinkstack.gen3

import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

import scala.concurrent.ExecutionContext
import scala.io.StdIn.readLine
import scala.concurrent.duration._
// import cats.effect.unsafe.implicits.global
import doobie.implicits._

object Main extends IOApp {

  def askName: IO[String] = IO {
    readLine("Name =>")
  }

  val upName: String => IO[String] = name =>
    IO.pure(name.toUpperCase)

  val program1: IO[ExitCode] = {
    for {
      _ <- IO {
        println("Hello Cats Effect")
      }
      name <- askName
      _ <- IO.sleep(1.second)
      // _ <- IO.interruptible(false)(Thread.sleep(1000))
      _ <- upName(name).map(println)

    } yield ExitCode.Success
  }

  val program2: IO[ExitCode] = for {
    fiber <- IO.println("Hello 👋").foreverM.startOn(ExecutionContext.global)
    _ <- IO.sleep(1.second)
    _ <- fiber.cancel
  } yield ExitCode.Success

  val program3: IO[ExitCode] = {
    final case class Currency(name: String)

    val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite:currencies.db")

    def all: doobie.ConnectionIO[List[Currency]] =
      sql"SELECT name FROM currencies".query[Currency].to[List]

    val namesFromDatabase: IO[List[Currency]] = all.transact(xa)

    for {
      names <- namesFromDatabase
      _ <- IO.println(names.map(currency => currency.copy(name = currency.name.toUpperCase)))
    } yield ExitCode.Success
  }

  override def run(args: List[String]): IO[ExitCode] = program3
}
