package com.pinkstack.bcmc

import cats.implicits._
import io.circe
import sttp.client3.circe.asJson
import sttp.client3.{HttpURLConnectionBackend, Identity, ResponseException, SttpBackend, basicRequest, _}
import sttp.model.Uri

object CoinMarketCapClient {

  import CoinMarketCap.Implicits._
  import CoinMarketCap._

  private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()
  private val initialParameters: Map[String, String] = Map(
    "limit" -> "200", "sortBy" -> "market_cap", "sortType" -> "desc",
    "convert" -> "EUR", "cryptoType" -> "all", "tagType" -> "all")

  private val baseUri: Uri = uri"https://api.coinmarketcap.com/data-api/v3/cryptocurrency/listing?$initialParameters"

  private val getCurrenciesPage: Map[String, String] => Either[ResponseException[String, circe.Error], CurrencyPage] =
    params =>
      basicRequest
        .get(baseUri.withParams(params))
        .response(asJson[CurrencyPage])
        .send(backend)
        .body

  val getAllCurrencies: Either[ResponseException[String, circe.Error], List[Currency]] = {
    val recordsPerPage = 200

    for {
      initialCurrencies <- getCurrenciesPage(Map("limit" -> recordsPerPage.toString))

      pages = Range(1, Math.ceil(initialCurrencies.totalCount / recordsPerPage).toInt + 1)
        .toList
        .map(page => Map(
          "start" -> (recordsPerPage * page + 1).toString,
          "limit" -> recordsPerPage.toString))

      otherCurrencies = pages.traverse(getCurrenciesPage)
        .foldMap(_.map(_.currencies))
        .foldK

    } yield initialCurrencies.currencies ++ otherCurrencies
  }
}
