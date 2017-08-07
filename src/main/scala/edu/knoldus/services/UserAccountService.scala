package edu.knoldus.services

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import edu.knoldus.models.Category

import scala.concurrent.duration.DurationInt
import edu.knoldus.{CustomerAccount, Database}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UserAccountService{

  def createAccount(listOfAccount: List[List[String]], accountGeneratorActorRef: ActorRef ): Future[Map[String,String]] ={

    implicit val timeout = Timeout(100 seconds)

    val listOfResult = for {
      accountInformation <- listOfAccount
      resultOfAccountCreation = (accountGeneratorActorRef ? accountInformation).mapTo[(String,String)]
    } yield resultOfAccountCreation

    Future.sequence(listOfResult).map(_.toMap)

  }

  def linkBillerToAccount(accountNumber: Long, name: String, category: Category.Value,
                          linkedBillerToAccountRef: ActorRef): Future[String] ={

    implicit val timeout = Timeout(100 seconds)
    (linkedBillerToAccountRef ? (accountNumber, name, category)).mapTo[String]

  }

}

