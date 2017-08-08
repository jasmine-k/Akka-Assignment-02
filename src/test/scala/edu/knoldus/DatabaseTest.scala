package edu.knoldus.actors
import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import edu.knoldus.{CustomerAccount, Database}
import edu.knoldus.models.{Category, LinkedBiller}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class DatabaseTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val database = new Database

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())

  val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.car, "CarBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, currentDate, 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, currentDate, 0.00, 0, 0, 0.00)
    )
  )

  val userAccountMapTest: mutable.Map[String, CustomerAccount] = mutable.Map(
    "jas" -> CustomerAccount(1L,"Jasmine","New Delhi","jas",2000.0),
    "sim" -> CustomerAccount(2L,"simran","New Delhi","sim",2000.0),
    "ruby" -> CustomerAccount(3L,"ruby","New Delhi","ruby",2000.0)
  )

  val customerAccount = CustomerAccount(100L, "Ram", "Delhi", "ram", 0.00)

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing getUserAccountMap") {

    assert(database.getUserAccount == userAccountMapTest)

  }

    test("Test for adding a user to database") {

      assert(database.addUserAccount("ram", customerAccount))

    }

  test("Testing getLinkedBiller") {

    assert(database.getLinkedBiller == linkedBiller)

  }


  test("Testing linkBiller where user already has other billers linked with him") {

    assert(database.linkBiller(1L, "TestBiller", Category.car))

  }

  test("Testing linkBiller where user does not have any biller linked with him") {

    assert(database.linkBiller(9L, "TestBiller", Category.electricity))

  }

  test("Testing depositSalary where user exists in Database") {

    assert(database.salaryDeposit(2L, "Simran", 90000.00))

  }

  test("Testing salaryDeposit where user doesn't exist in database") {

    assert(!database.salaryDeposit(194L, "tiwari", 10000.00))

  }

  test("Testing payBill for a user in Database not linked to a service") {

    assert(!database.payBill(1L, Category.food))

  }

  test("Testing payBill where amount to be paid exceeds account balance") {

    assert(!database.payBill(1L, Category.phone))

  }

  test("Testing payBill where user can successfully pay the bill") {

    assert(database.payBill(2L, Category.electricity))

  }


}
