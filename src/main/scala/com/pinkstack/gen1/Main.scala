package com.pinkstack.gen1

object Main {
  def main(args: Array[String]): Unit = {
    val names = List(
      "BitCoin", "dodoCoin", "dodo coin", "blockcoin", "cashman", "doller", "cashmachine",
      "loronum", "eterom", "eterim", "eterium", "coinblock", "blockchoin")

    val chain = MCGenerator.buildChain(names)

    val generatedNames = (1 to 500).map(_ => MCGenerator.generateBrand(chain, names)).toList

    println(generatedNames)
  }
}
