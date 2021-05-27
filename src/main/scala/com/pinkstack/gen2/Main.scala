/**
 * Author: Oto Brglez / @otobrglez - <otobrglez@gmail.com>
 */
package com.pinkstack.gen2

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair

import scala.jdk.CollectionConverters._
import scala.util.Random

trait Generator {
  type Chain[State] = Map[State, Map[State, Int]]

  private def pairs[S]: List[S] => List[List[S]] = _.sliding(2).toList

  private def transitions[S]: List[List[S]] => Chain[S] =
    _.foldLeft(Map.empty[S, Map[S, Int]]) {
      case (agg, Seq(current, next)) =>
        agg ++ Map(current -> agg.get(current).map { c =>
          c.get(next).map(frequency => c ++ Map(next -> (frequency + 1)))
            .getOrElse(c ++ Map(next -> 1))
        }.getOrElse(Map(next -> 1)))
      case (agg, _) => agg
    }

  private def randomSample[K, V <: Int](from: Map[K, V]): Option[K] =
    new EnumeratedDistribution(from.map(t => new Pair[K, java.lang.Double](t._1, t._2.toDouble)).toList.asJava)
      .sample(1).headOption.map(_.asInstanceOf[K])

  def chain[S]: List[S] => Chain[S] = pairs andThen transitions

  def generate[S](size: Int, initialState: Option[S] = None)(chain: Chain[S]): List[S] = {
    val firstState: S = initialState.getOrElse(Random.shuffle(chain.keys.toList).head)
    Range.inclusive(1, size).foldLeft(firstState :: Nil) { case (agg, _) =>
      val previous: S = agg.last
      val next: Option[S] = chain.get(previous).flatMap(randomSample)
      next.map(v => agg ++ (v :: Nil)).getOrElse(agg)
    }
  }

  def fromList[S](size: Int, list: List[S]): List[S] = generate(size)(chain(list))
}

object Generator extends Generator

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
