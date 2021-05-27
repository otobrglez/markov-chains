/**
 * Author: Oto Brglez / @otobrglez - <otobrglez@gmail.com>
 */
package com.pinkstack.gen2

object Main {
  def main(args: Array[String]): Unit = {
    type Weather = String
    val List(s, r, f) = List("🌞", "🌧", "🌫")
    val historicalWeather: List[Weather] = List(s, s, r, r, f, f, s, s, f, r, s, r, f, s, s, r, s)

    val weatherChain = Generator.chain(historicalWeather)
    val fakeWeather = Generator.generate(10)(weatherChain)

    println(s"Historical weather forecast\n${historicalWeather.mkString(", ")}")
    println(s"\nGenerated weather forecast\n${fakeWeather.mkString(", ")}")
    println(s"\nUnderlying chain\n$weatherChain")
    println(s"\nSimplification => ${Generator.fromList(5, historicalWeather)}")
  }
}
