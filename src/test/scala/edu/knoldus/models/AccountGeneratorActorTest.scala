
import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.FunSuiteLike
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.CustomerAccount
import edu.knoldus.actors.AccountGeneratorActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import scala.concurrent.ExecutionContext.Implicits.global

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
      val resturnMsg = msg match {
        case listOfInformation: List[String] => (customerAccount.userName, "Account created successfully!")
      }
      sender ! resturnMsg
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
      val resturnMsg = msg match {
        case listOfInformation: List[String] => "Username jasmine already exists! Try again with a different username"
      }
      sender ! resturnMsg
      TestActor.NoAutoPilot
    })

    accountGeneratorActorRef ! List("Jasmine", "New Delhi", "jasmine", "10.00")

    expectMsg("Username jasmine already exists! Try again with a different username")
  }

  test("Testing AccountGeneratorActor with invalid list") {

    accountGeneratorActorRef ! 1

    expectMsg("Invalid Information!")
  }

}