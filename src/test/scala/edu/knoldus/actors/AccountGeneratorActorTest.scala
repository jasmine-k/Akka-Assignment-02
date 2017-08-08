package edu.knoldus.actors


import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.CustomerAccount
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import org.scalatest.mockito.MockitoSugar

class AccountGeneratorActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceProbe = TestProbe()
  val accountGeneratorActorRef: ActorRef = system.actorOf(AccountGeneratorActor.props(databaseServiceProbe.ref))
  val customerAccount = CustomerAccount(1L, "Jasmine", "New Delhi", "jasmine", 0.00)

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return map containing status message for each account") {

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case listOfInformation: List[String] => (customerAccount.userName, "Account created successfully!")
      }
      sender ! returnMsg
      TestActor.NoAutoPilot
    })

    accountGeneratorActorRef ! List("Jasmine", "New Delhi", "jasmine", "10.00")

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "jasmine" &&
          resultMsg == "Account created successfully!")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case listOfInformation: List[String] => "Username jasmine already exists! Try again with a different username"
      }
      sender ! returnMsg
      TestActor.NoAutoPilot
    })

    accountGeneratorActorRef ! List("Jasmine", "New Delhi", "jasmine", "10.00")

    expectMsg("Username jasmine already exists! Try again with a different username")
  }

  test("Testing AccountGeneratorActor with invalid information") {

    accountGeneratorActorRef ! 1

    expectMsg("Invalid Information!")
  }

  test("Testing AccountGeneratorActor with invalid list values") {

    accountGeneratorActorRef ! List(1,2)

    expectMsg("Invalid Information!")
  }
}