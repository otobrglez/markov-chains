package com.pinkstack.gen5

import cats.data.State
import cats.implicits._
import com.pinkstack.gen5.Domain.Protocol._

object Interpreter {
  def applyCommand(command: Command): State[Domain.Survey, Event] =
    State { s =>
      command match {
        case CreateSurvey(name) =>
          val survey: Domain.Survey = s.copy(id = scala.util.Random.nextInt().some, name = name.some)
          (survey, Domain.Protocol.SurveyCreated(survey))

        case SetName(newName) =>
          val survey: Domain.Survey = s.copy(name = newName.some)
          (survey, Domain.Protocol.NameSet(newName))

        case AddQuestion(question: Domain.Incoming.Question) =>
          val newQuestion: Domain.Question = Domain.Question(
            scala.util.Random.nextInt().some,
            question.name.some,
            question.questionOptions.map { qo =>
              Domain.QuestionOption(scala.util.Random.nextInt().some, qo.name.some)
            }
          )

          val survey: Domain.Survey = s.copy(questions = s.questions ++ List(newQuestion))
          (survey, QuestionAdded(newQuestion))
      }
    }

  def run(commands: Domain.Protocol.Command*): Domain.Survey =
    commands.traverse(applyCommand).runEmptyS.value
}
