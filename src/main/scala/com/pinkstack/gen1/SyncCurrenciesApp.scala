package com.pinkstack.gen1

import cats.effect.IO
import cats.implicits._
import com.pinkstack.gen1.CoinMarketCap._
import com.typesafe.scalalogging.LazyLogging
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

// import scala.concurrent.ExecutionContext
import cats.effect.unsafe.implicits.global

object SyncCurrenciesApp extends LazyLogging {

  // implicit val cs = IO.contextShift(ExecutionContext.global)
  implicit val han = LogHandler.jdkLogHandler

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC", "jdbc:sqlite:currencies.db"
  )

  val createCurrenciesTable: Fragment =
    sql"""
        CREATE TABLE IF NOT EXISTS currencies (
            id INTEGER UNIQUE PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            slug VARCHAR(100) NOT NULL,
            symbol VARCHAR(10) NOT NULL,
            price DOUBLE PRECISION NOT NULL,
            created_at TEXT,
            updated_at TEXT
        )
         """

  val upsertCurrencies: List[Currency] => ConnectionIO[Int] = { currencies =>
    type CurrencyUpdate = (ID, Name, Slug, Symbol, Price, Price, ID)

    val upsert =
      """
        INSERT INTO currencies (id, name, slug, symbol, price, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, datetime('now'), datetime('now'))
        ON CONFLICT(id) DO UPDATE SET
            updated_at = datetime('now'),
            price = ?
            WHERE currencies.id = ?
        """

    Update[CurrencyUpdate](upsert).updateMany(currencies.map {
      case Currency(id, name, symbol, slug, price) =>
        (id, name, slug, symbol, price, price, id)
    })
  }

  def main(args: Array[String]): Unit =
    for {
      currencies <- CoinMarketCapClient.getAllCurrencies
      _ = logger.info("Processing currencies.")

      _ = createCurrenciesTable.update.run
        .transact(xa)
        .unsafeRunSync()

      _ = logger.info("Upserting currencies.")
      batches = currencies.grouped(100).toList
      inserts = batches.traverse { batch: List[Currency] =>
        upsertCurrencies(batch).transact(xa)
      }.unsafeRunSync()

      _ = logger.info(s"Updated or inserted currencies: ${inserts.sum}")
    } yield ()
}
