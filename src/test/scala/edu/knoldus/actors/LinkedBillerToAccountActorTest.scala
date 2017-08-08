package edu.knoldus.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.models.Category
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

class LinkedBillerToAccountActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {


  val databaseServiceProbe = TestProbe()
  val linkedBillerToAccountActorRef: ActorRef = system.actorOf(LinkedBillerToAccountActor.props(databaseServiceProbe.ref))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing LinkBillerToAccountActor and linking an account with a biller")
  {
    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "Successfully Linked your account with the given biller!"
      }
      sender ! returnMsg
      TestActor.NoAutoPilot
    })

    linkedBillerToAccountActorRef ! (20L, "TestingBiller", Category.car)

    expectMsg("Successfully Linked your account with the given biller!")

  }

  test("Testing LinkBillerToAccountActor with a link already existing")
  {
    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "You are already linked to the given biller!"
      }
      sender ! returnMsg
      TestActor.NoAutoPilot
    })

    linkedBillerToAccountActorRef ! (20L, "TestingBiller", Category.car)

    expectMsg("You are already linked to the given biller!")

  }

  test("Testing LinkBillerToAccountActor for invalid information")
  {
    linkedBillerToAccountActorRef ! (1, 2, 3)

    expectMsg("Invalid information received while linking!")
  }

}
