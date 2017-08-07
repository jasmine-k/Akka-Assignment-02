package edu.knoldus.models

import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.FunSuiteLike
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.services.UserAccountService
import org.scalatest.BeforeAndAfterAll

class UserAccountServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender {

  val userAccountServiceObject = new UserAccountService

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing createAccount for UserAccountService")
  {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case listOfAccountInformation: List[String] => (listOfAccountInformation(2), "Account created successfully!")
      }
      sender ! (returnMsg)
      TestActor.KeepRunning
    })
    val listOAccountInformaion = List(List("Jasmine", "New Delhi", "jasmine", "10.00"),
      List("Jasmine", "New Delhi", "jasmine", "10.00"))
    userAccountServiceObject.createAccount(listOAccountInformaion, probe.ref).map(result =>
      assert(
        result == Map(
          "jasmine" -> "Account created successfully!!", "jasmine" -> "Account created successfully!!"
        )
      ))
  }

  test("Testing linking of biller and account")
  {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, billerName: String, billerCategory: Category.Value) => "Successfully Linked your account with the given biller!"
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    userAccountServiceObject.linkBillerToAccount(1L, "CarBiller", Category.car, probe.ref).map(
      resultMsg => assert(resultMsg == "Successfully Linked your account with the given biller!")
    )

  }

}
