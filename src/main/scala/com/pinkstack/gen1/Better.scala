package com.pinkstack.gen1

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

trait Better[S] {
  type Chain = Map[S, Map[S, Int]]

  def buildChain[I](list: List[S]): Chain

  val xChain: List[S] => Chain

  def pairs[C <: IterableOnce[S]](states: C): List[List[S]] =
    states.iterator.sliding(2).toList.map(_.toList)
}
