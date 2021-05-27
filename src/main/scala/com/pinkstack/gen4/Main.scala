package com.pinkstack.gen4

import cats.effect.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.pinkstack.gen1.CoinMarketCap.Implicits._
import com.pinkstack.gen1.CoinMarketCap._
import sttp.client3._
import sttp.client3.circe._
import sttp.model.Uri

object CoinMarketCapClient {
  type PerPage = Int
  val DefaultPerPage: PerPage = 200
  type QueryKey = String
  type QueryValue = String
  type QueryParams = Map[QueryKey, QueryValue]
  implicit val intToQueryValue: Int => QueryValue = _.toString

  private def queryParams(p: (QueryKey, QueryValue)*): QueryParams = p.toMap

  private val initialParameters: QueryParams = queryParams(
    "limit" -> 200, "sortBy" -> "market_cap", "sortType" -> "desc",
    "convert" -> "EUR", "cryptoType" -> "all", "tagType" -> "all")

  private val baseUri: Uri = uri"https://api.coinmarketcap.com/data-api/v3/cryptocurrency/listing?$initialParameters"

  private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  private val getCurrenciesPage: QueryParams => IO[CurrencyPage] =
    params =>
      IO.fromEither(
        basicRequest
          .get(baseUri.withParams(params))
          .response(asJson[CurrencyPage])
          .send(backend)
          .body)

  private def headCurrencies(implicit recordsPerPage: Int): IO[CurrencyPage] =
    getCurrenciesPage(queryParams("limit" -> recordsPerPage))

  private def tailCurrencies(perPage: PerPage)(head: CurrencyPage): IO[List[CurrencyPage]] =
    Range(1, Math.ceil(head.totalCount / perPage).toInt + 1)
      .map(page => queryParams(
        "start" -> (perPage * page + 1),
        "limit" -> perPage))
      .toList
      .parTraverseN(4)(getCurrenciesPage)

  private def allResponses(perPage: PerPage = DefaultPerPage): IO[List[CurrencyPage]] =
    for {
      perPage <- IO.pure(perPage)
      head <- headCurrencies(perPage)
      tail <- tailCurrencies(perPage)(head)
      all <- IO.pure(List(head) ++ tail)
    } yield all

  def currencies(perPage: PerPage): IO[List[Currency]] =
    allResponses(perPage).map(_.map(_.currencies).foldK)
}

object Main extends IOApp {
  val program: IO[ExitCode] =
    for {
      currencies <- CoinMarketCapClient.currencies(300)
      _ <- IO.println {
        s"Got ${currencies.size} currencies."
      }
    } yield ExitCode.Success

  def run(args: List[String]): IO[ExitCode] = program
}
