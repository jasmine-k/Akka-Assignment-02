package edu.knoldus.models

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import edu.knoldus.CustomerAccount

class AccountGeneratorActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging with RequiresMessageQueue[BoundedMessageQueueSemantics] {

  var accountNumber = 0

  override def receive: PartialFunction[Any,Unit] = {
    case listOfInformation : List[String] =>
      accountNumber = accountNumber + 1
      log.info("Assigning Account Number and forwarding request to Database Service")
      val updatedListOfInformation = (accountNumber.toString :: listOfInformation).map(_.toString)
      databaseServiceActorRef.forward(updatedListOfInformation)

    case _ =>
      log.error("Invalid User Information!!")
  }
}

object AccountGeneratorActor {

  def props(databaseServiceActorRef: ActorRef): Props =
    Props(classOf[AccountGeneratorActor], databaseServiceActorRef)
}

