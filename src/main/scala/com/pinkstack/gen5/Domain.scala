package com.pinkstack.gen5

import cats._
import cats.implicits._

object Domain {
  type DomainObjectID = Int
  type SurveyID = DomainObjectID
  type QuestionID = DomainObjectID
  type QuestionOptionID = DomainObjectID

  object Protocol {
    sealed trait Event extends Product

    sealed trait Command extends Product

    final case class CreateSurvey(name: String) extends Command

    final case class SurveyCreated(survey: Domain.Survey) extends Event

    final case class SetName(newName: String) extends Command

    final case class NameSet(name: String) extends Event

    final case class AddQuestion(question: Incoming.Question) extends Command

    final case class QuestionAdded(question: Domain.Question) extends Event

  }

  object Incoming {
    sealed trait DomainObject

    final case class QuestionOption(name: String) extends DomainObject

    final case class Question(name: String, questionOptions: List[QuestionOption] = List.empty) extends DomainObject
  }

  object Domain {
    sealed trait DomainObject {
      def id: Option[DomainObjectID]
    }

    final case class Survey(id: Option[SurveyID], name: Option[String], questions: List[Question] = List.empty) extends DomainObject

    object Survey {
      val empty: Survey = Survey(None, None)
    }

    final case class QuestionOption(id: Option[QuestionOptionID], name: Option[String]) extends DomainObject

    object QuestionOption {
      val empty: QuestionOption = QuestionOption(None, None)
    }

    final case class Question(id: Option[QuestionID], name: Option[String], questionOptions: List[QuestionOption] = List.empty) extends DomainObject

    object Question {
      val empty: Question = Question(None, None)
    }

    implicit val surveyMonoid: Monoid[Survey] = new Monoid[Survey] {
      override def empty: Survey = Survey.empty

      override def combine(x: Survey, y: Survey): Survey =
        Survey(
          id = x.id |+| y.id,
          name = x.name |+| y.name,
          questions = x.questions |+| y.questions
        )
    }
  }
}
