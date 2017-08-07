package edu.knoldus.models
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import org.scalatest.mockito.MockitoSugar


class BillProcessingActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceActor = TestProbe()
  val billProcessingActor = system.actorOf(BillProcessActor.props(databaseServiceActor.ref))

  databaseServiceActor.setAutoPilot((sender: ActorRef, msg: Any) => {
    val resturnMsg = msg match {
      case (accountNo: Long, billToPay: Double, billerCategory: Category.Value) => "Bill successfully paid"
    }
    sender ! resturnMsg
    TestActor.KeepRunning
  })

  test("Testing for phone category") {

    billProcessingActor ! (100L, Category.phone)

    expectMsg("Bill successfully paid")

  }

  test("Testing for car category") {

    billProcessingActor ! (100L, Category.phone)

    expectMsg("Bill successfully paid")

  }

  test("Testing for internet category") {

    billProcessingActor ! (100L, Category.phone)

    expectMsg("Bill successfully paid")

  }

  test("Testing for food category") {

    billProcessingActor ! (100L, Category.phone)

    expectMsg("Bill successfully paid")

  }

  test("Testing for electricity category") {

    billProcessingActor ! (100L, Category.phone)

    expectMsg("Bill successfully paid")

  }

  test("Testing with invalid information")
  {
    billProcessingActor ! (1,2)

    expectMsg("Invalid information received")
  }

}
