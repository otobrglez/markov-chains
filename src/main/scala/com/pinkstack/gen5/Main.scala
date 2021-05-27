package com.pinkstack.gen5

import cats.data.State
import cats.implicits._
import com.pinkstack.gen5.Domain.Protocol._
import io.circe.generic.auto._
import io.circe.syntax._

object Interpreter {
  def applyCommand(command: Command): State[Domain.Domain.Survey, Event] =
    State { s =>
      command match {
        case CreateSurvey(name) =>
          val survey: Domain.Domain.Survey = s.copy(id = scala.util.Random.nextInt().some, name = name.some)
          (survey, Domain.Protocol.SurveyCreated(survey))

        case SetName(newName) =>
          val survey: Domain.Domain.Survey = s.copy(name = newName.some)
          (survey, Domain.Protocol.NameSet(newName))

        case AddQuestion(question: Domain.Incoming.Question) =>
          val newQuestion: Domain.Domain.Question = Domain.Domain.Question(
            id = scala.util.Random.nextInt().some,
            name = question.name.some)

          val survey: Domain.Domain.Survey = s.copy(questions = s.questions ++ List(newQuestion))
          (survey, QuestionAdded(newQuestion))
      }
    }

  def run(commands: Domain.Protocol.Command*): Domain.Domain.Survey =
    commands.traverse(applyCommand).runEmptyS.value
}

object Main {

  import Domain.Incoming._
  import Domain.Protocol._

  def main(args: Array[String]): Unit = {
    val commands: List[Command] = List(
      CreateSurvey("Hello"),
      SetName("This is another name"),
      SetName("Final name"),
      AddQuestion(Question("What is your gender?", List(QuestionOption("Male"), QuestionOption("Female")))),
      AddQuestion(Question("Favorite color?", List(QuestionOption("Green"), QuestionOption("Red"), QuestionOption("Blue")))),
    )

    val survey = Interpreter.run(commands: _*)

    println {
      survey.asJson
    }
  }
}
