package com.pinkstack.gen1

import io.circe.{Decoder, HCursor}

object CoinMarketCap {
  type ID = Int
  type Name = String
  type Symbol = String
  type Slug = String
  type Price = Double

  final case class Currency(id: ID, name: Name, symbol: Symbol, slug: Slug, price: Price)

  final case class CurrencyPage(totalCount: Int, currencies: List[Currency] = List.empty)

  object Implicits {
    implicit val decodeCurrency: Decoder[Currency] = (c: HCursor) => {
      for {
        id <- c.downField("id").as[ID]
        name <- c.downField("name").as[Name]
        symbol <- c.downField("symbol").as[Symbol]
        slug <- c.downField("slug").as[Slug]
        price <- c.downField("quotes").downN(0).downField("price").as[Price]
      } yield Currency(id, name, symbol, slug, price)
    }

    implicit val decodeCurrencyList: Decoder[CurrencyPage] = (c: HCursor) => for {
      totalCount <- c.downField("data").downField("totalCount").as[Int]
      currencies <- c.downField("data").downField("cryptoCurrencyList").as[List[Currency]]
    } yield CurrencyPage(totalCount, currencies)
  }
}
