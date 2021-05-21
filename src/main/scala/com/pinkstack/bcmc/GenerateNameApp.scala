package com.pinkstack.bcmc

import cats.effect.IO
import com.pinkstack.bcmc.Better.Implicits.{CharChain, StringChain}
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair

import scala.jdk.CollectionConverters._
import scala.util.Random


trait MCGenerator {
  type MChain[State] = Map[State, Map[State, Int]]

  private def randomSample[K, V <: Int](f: PartialFunction[(K, V), Pair[K, java.lang.Double]])
                                       (value: Map[K, V]): Option[K] =
    new EnumeratedDistribution(value.map(f).toList.asJava)
      .sample(1)
      .headOption
      .map(_.asInstanceOf[K])

  val pairs: List[String] => List[List[Char]] = names =>
    for {
      name <- names
      tuples <- name.sliding(2).map(_.toCharArray.toList).toList
    } yield tuples

  val transitions: List[List[Char]] => MChain[Char] =
    _.foldLeft(Map.empty[Char, Map[Char, Int]]) {
      case (agg, Seq(current, next)) =>
        agg ++ Map(current -> agg.get(current).map { c =>
          c.get(next)
            .map(frequency => c ++ Map(next -> (frequency + 1)))
            .getOrElse(c ++ Map(next -> 1))
        }.getOrElse(Map(next -> 1)))
    }

  def generateWord(chain: MChain[Char])(length: Int)(implicit seedChar: Option[Char] = None): String = {
    val firstChar: Char = seedChar.getOrElse(Random.shuffle(chain.keys.toList).head)

    Range.inclusive(1, length).foldLeft(firstChar :: Nil) { case (agg, _) =>
      val next = chain.get(agg.last).flatMap(randomSample {
        case (k, v) => new Pair[Char, java.lang.Double](k, v.toDouble)
      }).getOrElse(' ')
      agg ++ (next :: Nil)
    }.mkString("").trim
  }

  def rollup[K, NK, V](f: V => NK)(list: List[V]): Map[NK, Int] =
    list.foldLeft(Map.empty[NK, Int]) { case (agg, s) =>
      val key: NK = f(s)
      agg ++ Map(key -> agg.get(key).map(_ + 1).getOrElse(1))
    }

  val namesSizes: List[String] => Map[Int, Int] =
    rollup(_.length)

  val wordsSizes: List[String] => Map[Int, Int] =
    rollup(_.split("\\s+").length)

  val buildChain: List[String] => Map[Char, Map[Char, Int]] =
    transitions compose pairs

  val viaValue: PartialFunction[(Int, Int), Pair[Int, java.lang.Double]] = {
    case (k: Int, v: Int) => new Pair[Int, java.lang.Double](k, v.toDouble)
  }

  val pickLength: List[String] => Option[Int] = namesSizes andThen randomSample(viaValue)

  val pickNumberOfWords: List[String] => Option[Int] = wordsSizes andThen randomSample(viaValue)

  val numberOfWords: List[String] => Range = names => 1 to pickNumberOfWords(names).getOrElse(1)

  val generateBrand: (MChain[Char], List[String]) => String = {
    case (chain, names) =>
      numberOfWords(names).flatMap(_ => pickLength(names).map(generateWord(chain))).mkString("")
  }
}

object MCGenerator extends MCGenerator

trait Better[S] {
  type Chain = Map[S, Map[S, Int]]

  def buildChain[I](list: List[S]): Chain

  val xChain: List[S] => Chain

  def pairs[C <: IterableOnce[S]](states: C): List[List[S]] =
    states.iterator.sliding(2).toList.map(_.toList)
}

object Better {

  object Implicits {
    implicit object StringChain extends Better[String] {
      def buildChain[I](list: List[String]): StringChain.Chain = Map("oto" -> Map("m" -> 1))

      override val xChain: List[String] => StringChain.Chain = _ =>
        Map("oto" -> Map("m" -> 1))
    }

    implicit object CharChain extends Better[Char] {
      def buildChain[I](list: List[Char]): CharChain.Chain = Map('o' -> Map('m' -> 1))

      override val xChain: List[Char] => CharChain.Chain = _ =>
        Map('o' -> Map('m' -> 1))
    }
  }

  def chain[T](list: List[T])(implicit bc: Better[T]): bc.Chain = {
    println(bc.pairs(list))
    bc.buildChain(list)
  }
}

object GenerateNameApp {
  def main(args: Array[String]): Unit = {
    // import Better.Implicits._

    val names = List(
      "BitCoin", "dodoCoin", "dodo coin", "blockcoin", "cashman", "doller", "cashmachine",
      "loronum", "eterom", "eterim", "eterium", "coinblock", "blockchoin")

    val chainNames = Better.chain(names)
    println(chainNames)
    // val chain = BetterGenerator.buildChain(names)
    // println(chain)
    // (1 to 500).foreach { _ =>
    //   println(MCGenerator.generateBrand(chain, names))
    // }

    println("----")

    type W = Char
    val R: W = 'R'
    val S: W = 'S'
    val F: W = 'F'
    val weather = List[W](R, R, F, S, S, S, S, S, F, R, R, R, R, F, S)

    val weatherChain = Better.chain(weather)
    println(weatherChain)

  }
}
