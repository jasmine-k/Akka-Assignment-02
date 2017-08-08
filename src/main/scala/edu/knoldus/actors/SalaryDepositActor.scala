package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import edu.knoldus.models.Category

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


class SalaryDepositActor(databaseServiceActorRef:ActorRef ) extends Actor with ActorLogging{
  override def receive: Receive = {
    case (accountNumber: Long, name: String,salary: Double) =>

      databaseServiceActorRef.forward(accountNumber, name, salary)
      implicit val timeout = Timeout(100 seconds)
      log.info("Sender before ask is " + sender())
      val listOfBillers = (databaseServiceActorRef ? accountNumber).mapTo[mutable.ListBuffer[Category.Value]]
      log.info("Sender after ask is " + sender())
      val senderInSalaryDepositor = sender().actorRef
      listOfBillers onComplete {

        case Success(value) => log.info("Sender in onComplete is " + senderInSalaryDepositor)
          value.foreach(billerCategory =>
            context.actorOf(BillProcessActor.props(databaseServiceActorRef)).tell((accountNumber, billerCategory), senderInSalaryDepositor))

        case Failure(ex) => log.info("Failed while receiving listOfBillers with exception " + ex)

      }
  }

}

object SalaryDepositActor {
  def props(databaseServiceActorRef: ActorRef): Props =
    Props(classOf[SalaryDepositActor], databaseServiceActorRef)

}

class BillProcessActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {


  override def receive: Receive = {


    case (accountNo: Long, billerCategory: Category.Value) =>
      implicit val timeout = Timeout(100 seconds)
      billerCategory match {

        case Category.car => databaseServiceActorRef.forward(accountNo, Category.car)
        case Category.phone => databaseServiceActorRef.forward(accountNo, Category.phone)
        case Category.internet => databaseServiceActorRef.forward(accountNo, Category.internet)
        case Category.electricity => databaseServiceActorRef.forward(accountNo, Category.electricity)
        case Category.food => databaseServiceActorRef.forward(accountNo, Category.food)

      }
    case _ => sender() ! "Invalid information received"
  }
}

object BillProcessActor {
  def props(databaseServiceActorRef:ActorRef): Props =
    Props(classOf[BillProcessActor], databaseServiceActorRef)

}
