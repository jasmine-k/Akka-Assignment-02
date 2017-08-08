package edu.knoldus.models

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


class SalaryDepositActor(databaseServiceActorRef:ActorRef ) extends Actor with ActorLogging{
  override def receive: Receive = {
    case (accountNumber: Long, name: String,salary: Double) =>
      log.info("Transferring salary to account ! ")
      implicit val timeout = Timeout(100 seconds)
      databaseServiceActorRef.forward(accountNumber, name, salary)

      //implicit val timeout = Timeout(100 seconds)
      val listOfBillers = (databaseServiceActorRef ? accountNumber).mapTo[mutable.ListBuffer[Category.Value]]
      listOfBillers onComplete {

        case Success(value) =>log.info("Sender in onComplete is " + sender())
          value.foreach(billerCategory => context.actorOf(BillProcessActor.props(databaseServiceActorRef)).forward(accountNumber, billerCategory))

        case Failure(ex) => log.info("Failed while receiving listOfBillers with exception " + ex)

      }

    case _ =>
      log.error("Invalid Information received while salary depositing")
      sender()! "Invalid Information received while salary depositing"
  }

}

object SalaryDepositActor {
  def props(databaseServiceActorRef: ActorRef): Props =
    Props(classOf[SalaryDepositActor], databaseServiceActorRef)

}

class BillProcessActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {

  val CAR_BILL: Double = 100
  val PHONE_BILL: Double = 200
  val INTERNET_BILL: Double = 300
  val ELECTRICITY_BILL: Double = 400
  val FOOD_BILL: Double = 500

  override def receive: Receive = {


    case (accountNo: Long, billerCategory: Category.Value) =>
      implicit val timeout = Timeout(100 seconds)

      billerCategory match {

        case Category.car => databaseServiceActorRef.forward(accountNo, CAR_BILL)
        case Category.phone => databaseServiceActorRef.forward(accountNo, PHONE_BILL)
        case Category.internet => databaseServiceActorRef.forward(accountNo, INTERNET_BILL)
        case Category.electricity => databaseServiceActorRef.forward(accountNo, ELECTRICITY_BILL)
        case Category.food => databaseServiceActorRef.forward(accountNo, FOOD_BILL)

      }
  }
}

object BillProcessActor {
  def props(databaseServiceActorRef:ActorRef): Props =
    Props(classOf[BillProcessActor], databaseServiceActorRef)

}
