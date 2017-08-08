package edu.knoldus.services

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import scala.concurrent.ExecutionContext.Implicits.global



class SalaryDepositServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val salaryDepositorActorProbe = TestProbe()
  val salaryDepositServiceObj = new SalaryDepositService

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing SalaryDepositService salaryDeposit method")
  {

    salaryDepositorActorProbe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (accountNo: Long, customerName: String, salary: Double) => true
      }
      sender ! resturnMsg
      TestActor.KeepRunning
    })

    val futureOfBoolean = salaryDepositServiceObj.salaryDeposit(1L, "Jasmine", 10000.00, salaryDepositorActorProbe.ref)

    futureOfBoolean.map(value => assert(value == "Salary deposited successfully"))
  }
}
