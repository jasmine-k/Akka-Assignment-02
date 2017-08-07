package edu.knoldus.models
import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.apache.log4j.Logger
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import org.mockito.Mockito._



class LinkBillerToAccountActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceProbe = TestProbe()
  val linkedBillerToAccountActorRef: ActorRef = system.actorOf(LinkedBillerToAccountActor.props(databaseServiceProbe.ref))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing LinkBillerToAccountActor and linking an account with a biller")
  {
    linkedBillerToAccountActorRef ! (1L, "TestingBiller", Category.food)

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "Linked to biller successfully!!"
      }
      sender ! returnMsg
      TestActor.KeepRunning
    })

    expectMsg("Linked to biller successfully!!")

  }

  test("Testing LinkBillerToAccountActor with a link already existing")
  {
    linkedBillerToAccountActorRef ! (1L, "TestingBiller", Category.food)

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
          "Already linked to the biller!!"
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    expectMsg("Already linked to the biller!!")

  }

  test("Testing LinkBillerToAccountActor for invalid information")
  {
    linkedBillerToAccountActorRef ! (1, 2, 3)

    expectMsg("Invalid information received while linking!")
  }

}
