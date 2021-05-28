package com.pinkstack.gen5

import io.circe.generic.auto._
import io.circe.syntax._

object Main {

  import Domain.Incoming._
  import Domain.Protocol._

  def main(args: Array[String]): Unit = {
    val commands: List[Command] = List(
      CreateSurvey("Hello"),
      SetName("This is another name"),
      SetName("Final name"),
      AddQuestion(Question("What is your gender?",
        List(
          QuestionOption("Male"),
          QuestionOption("Female"))
      )),
      AddQuestion(Question("Favorite color?",
        List(
          QuestionOption("Green"),
          QuestionOption("Red"),
          QuestionOption("Blue"))
      )),
      SetName("Example Survey I.")
    )

    val survey = Interpreter.run(commands: _*)

    println {
      survey.asJson
    }
  }
}
