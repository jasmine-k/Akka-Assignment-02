package edu.knoldus.models

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import edu.knoldus.services.DatabaseService

class LinkedBillerToAccountActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNumber: Long, name: String, category: Category.Value) =>
      log.info("Forwarding request to Database Service")
      databaseServiceActorRef.forward(accountNumber, name, category)

    case _ =>
      log.error("Invalid information received!")
      sender() ! "Invaid information received!"
  }

}

object LinkedBillerToAccountActor {

  def props(databaseServiceActorRef:ActorRef):Props =
    Props(classOf[LinkedBillerToAccountActor], databaseServiceActorRef)
}
