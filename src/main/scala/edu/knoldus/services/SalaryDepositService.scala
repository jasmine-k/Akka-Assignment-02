package edu.knoldus.services

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern._
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future

class SalaryDepositService {

  def salaryDeposit(accountNumber: Long, name: String,salary: Double,
                    salaryDepositActorRef: ActorRef): Future[Boolean] = {

    implicit val timeout = Timeout(100 seconds)
     (salaryDepositActorRef ? (accountNumber, name,salary)).mapTo[Boolean]

  }

}
