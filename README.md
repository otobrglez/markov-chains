# Markov Chains with Scala

![graph](markov-chain-example.svg)

Exploring different concepts and playing around with [Markov Chains][mc] and other FP stuff in Scala.

The code was also written as part of my exploration of [Twitch - @otobrglez](https://www.twitch.tv/otobrglez).

## Examples

### [gen1](src/main/scala/com/pinkstack/gen1) - Pulling crypto-currencies and generating names

```bash
$ sbt "runMain com.pinkstack.gen1.SyncCurrenciesApp"
$ sbt "runMain com.pinkstack.gen1.GenerateNameApp"
```

### [gen2](src/main/scala/com/pinkstack/gen2) - Clean and simple FP implementation

```bash
$ sbt "runMain com.pinkstack.gen1.Main"
```

### [gen3](src/main/scala/com/pinkstack/gen3) - Playing with Cats Effect 3

```bash
$ sbt "runMain com.pinkstack.gen3.Main"
```

### [gen4](src/main/scala/com/pinkstack/gen4) - Getting crypto-currencies and syncing them locally in SQLite.

Focus here was elegant usage of [Cats Effect 3][ce3] w/ [Doobie][doobie] and [sttp] 
with [IO](https://typelevel.org/cats-effect/docs/2.x/datatypes/io) data type.

```bash
$ sbt "runMain com.pinkstack.gen4.Main"
```


## Resources

- https://en.wikipedia.org/wiki/Markov_chain
- https://brilliant.org/wiki/markov-chains/
- https://towardsdatascience.com/introduction-to-markov-chains-50da3645a50d

[mc]: https://en.wikipedia.org/wiki/Markov_chain

## Author

- [Oto Brglez](https://github.com/otobrglez) / [@otobrglez](https://twitter.com/otobrglez)

[ce3]: https://typelevel.org/cats-effect/
[doobie]: https://tpolecat.github.io/doobie/
[sttp]: https://github.com/softwaremill/sttp
