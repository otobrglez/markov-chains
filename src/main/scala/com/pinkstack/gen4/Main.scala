package com.pinkstack.gen4

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.pinkstack.gen1.CoinMarketCap._
import com.typesafe.scalalogging.LazyLogging
import doobie.implicits._

import scala.concurrent.duration._

object Main extends IOApp with LazyLogging {
  val log: String => IO[Unit] = s => IO(logger.info(s)).void

  def refreshLocalCurrencies: IO[Unit] =
    for {
      currencies <- CoinMarketCapClient.currencies(300)
      inserts <- currencies
        .grouped(200)
        .toList
        .traverse { batch: List[Currency] =>
          DB.upsertCurrencies(batch).transact(DB.xa)
        }
      _ <- log(s"Upserts: ${inserts.sum}")
    } yield IO.sleep(1.seconds)

  val program: IO[ExitCode] =
    for {
      _ <- log("Getting started here")
      _ <- DB.createCurrenciesTable.update.run.transact(DB.xa)
      fiber <- refreshLocalCurrencies.delayBy(1.second).foreverM.start
      _ <- fiber.join
    } yield ExitCode.Success

  def run(args: List[String]): IO[ExitCode] = program
}
