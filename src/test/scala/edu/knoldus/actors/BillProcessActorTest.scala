package edu.knoldus.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.models.Category
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}


class BillProcessingActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceActor = TestProbe()
  val billProcessingActor = system.actorOf(BillProcessActor.props(databaseServiceActor.ref))

  databaseServiceActor.setAutoPilot((sender: ActorRef, msg: Any) => {
    val returnMsg = msg match {
      case (accountNumber: Long, billerCategory: Category.Value) => "Bill Paid Successfully"
    }
    sender ! returnMsg
    TestActor.KeepRunning
  })

  test("Testing for internet category") {

    billProcessingActor ! (20L, Category.phone)

    expectMsg("Bill Paid Successfully")

  }

  test("Testing for phone category") {

    billProcessingActor ! (20L, Category.phone)

    expectMsg("Bill Paid Successfully")

  }

  test("Testing for car category") {

    billProcessingActor ! (30L, Category.phone)

    expectMsg("Bill Paid Successfully")

  }

  test("Testing for food category") {

    billProcessingActor ! (40L, Category.phone)

    expectMsg("Bill Paid Successfully")

  }

  test("Testing for electricity category") {

    billProcessingActor ! (10L, Category.phone)

    expectMsg("Bill Paid Successfully")

  }

  test("Testing with invalid information")
  {
    billProcessingActor ! (1,2)

    expectMsg("Invalid information received")
  }

}
