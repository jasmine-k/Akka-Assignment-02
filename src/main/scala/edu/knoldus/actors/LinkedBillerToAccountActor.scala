package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import edu.knoldus.models.Category

import scala.concurrent.duration.DurationInt

class LinkedBillerToAccountActor(databaseServiceActorRef: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNumber: Long, name: String, category: Category.Value) =>
      log.info("Forwarding request to Database Service")
      implicit val timeout = Timeout(100 seconds)
      databaseServiceActorRef.forward(accountNumber, name, category)

    case _ =>
      log.error("Invalid information received while linking!")
      sender() ! "Invalid information received while linking!"
  }

}

object LinkedBillerToAccountActor {

  def props(databaseServiceActorRef:ActorRef):Props =
    Props(classOf[LinkedBillerToAccountActor], databaseServiceActorRef)

}
