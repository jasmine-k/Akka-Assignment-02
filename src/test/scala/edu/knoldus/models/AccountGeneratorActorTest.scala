
import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.FunSuiteLike
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.CustomerAccount
import edu.knoldus.models.AccountGeneratorActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import scala.concurrent.ExecutionContext.Implicits.global



class AccountGeneratorActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val databaseServiceProbe = TestProbe()
  val accountGeneratorActorRef: ActorRef = system.actorOf(AccountGeneratorActor.props(databaseServiceProbe.ref))

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing AccountGeneratorActor which should return map containing status message for each account") {

    val customerAccount = CustomerAccount(1L, "Jasmine", "New Delhi", "ruby", 0.00)

    accountGeneratorActorRef ! List("Jasmine", "New Delhi", "ruby", "10.00")

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case listOfInformation: List[String] => (customerAccount.userName, "Account created successfully!")
      }
      sender ! returnMsg
      TestActor.KeepRunning
    })

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "ruby" &&
          resultMsg == "Account created successfully!")
    }
  }

  test("Testing AccountGeneratorActor with existing username") {

    accountGeneratorActorRef ! List("jasmine", "New Delhi", "ruby", "10.00")

    databaseServiceProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case listOfInformation: List[String] => ("ruby", "Username already exists!!")
      }
      sender ! returnMsg
      TestActor.KeepRunning
    })

    expectMsgPF() {
      case (username: String,resultMsg: String) => assert(username == "ruby" &&
        resultMsg == "Username already exists!!")
    }
  }

  test("Testing AccountGeneratorActor with invalid list") {

    accountGeneratorActorRef ! List(1,2,3,4,5)

    expectMsg("Invalid User Information!!")
  }

}
