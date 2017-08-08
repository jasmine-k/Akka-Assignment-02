import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import edu.knoldus.{CustomerAccount, Database}
import edu.knoldus.actors.DatabaseServiceActor
import edu.knoldus.models.{Category, LinkedBiller}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import org.mockito.Mockito._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer



class DatabaseServiceActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with MockitoSugar {

  val database = mock[Database]
  val databaseServiceActor = system.actorOf(DatabaseServiceActor.props)

  val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = mutable.Map(
    1L -> ListBuffer(
      LinkedBiller(Category.phone, "PhoneBiller", 1L, "date", 0.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, "date", 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, "date", 0.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, "date", 0.00, 0, 0, 0.00)
    )
  )

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  test("Testing DatabaseServiceActor for creating an account")
  {

    val listOfInformation = List("1", "Jasmine", "New Delhi", "jasmine", "100.00")

    when(database.getUserAccount).thenReturn(mutable.Map(
      "jasmine" -> CustomerAccount(1L, "Jasmine", "New Delhi", "jasmine", 100.00),
      "sim" -> CustomerAccount(2L, "Simran", "New Delhi", "sim", 0.00)
    ))

    val customerAccount = CustomerAccount(listOfInformation)
    when(database.addUserAccount(customerAccount.userName , customerAccount)).thenReturn(true)

    databaseServiceActor ! listOfInformation

    expectMsgPF() {

      case (username: String, msg: String) => assert(username == customerAccount.userName &&
        msg == "Account created successfully!!")

    }

  }

  test("Testing DatabaseServiceActor for creating an account with existing username")
  {

    val listOfInformation = List("1", "Jasmine", "New Delhi", "jasmine", "10.00")

    when(database.getUserAccount).thenReturn(mutable.Map(
      "jasmine" -> CustomerAccount(1L, "Jasmine", "New Delhi", "jasmine", 10.00),
      "sim" -> CustomerAccount(2L, "Simran", "New Delhi", "sim", 0.00)
    ))

    databaseServiceActor ! listOfInformation

    expectMsgPF() {

      case (username: String, msg: String) => assert(username == "jasmine" &&
        msg == "Username already exists!!")

    }

  }

  test("Testing DatabaseServiceActor for linking account to biller")
  {

    when(database.getLinkedBiller).thenReturn(linkedBiller)

    when(database.linkBiller(100L, "TestingBiller", Category.phone)).thenReturn(true)

    databaseServiceActor ! (100L, "TestingBiller", Category.phone)

    expectMsg("Linked to biller successfully!!")

  }

  test("Testing DatabaseServiceActor for linking where link already exists")
  {

    when(database.getLinkedBiller).thenReturn(linkedBiller)

    databaseServiceActor ! (1L, "TestingBiller", Category.car)

    expectMsg("Already linked to the biller!!")

  }

  test("Testing DatabaseServiceActor for returning account number when username is given")
  {

    val userAccountMap: mutable.Map[String, CustomerAccount] = mutable.Map(
      "jasmine" -> CustomerAccount(1L, "Jasmine", "New Delhi", "jasmine", 100.00),
      "sim" -> CustomerAccount(2L, "Simran", "New Delhi", "sim", 0.00)
    )

    when(database.getUserAccount).thenReturn(userAccountMap)

    databaseServiceActor ! "jasmine"

    expectMsgPF() {
      case accountNo: Long => assert(accountNo == 1L)
    }

  }

  test("Testing DatabaseServiceActor for depositing salary")
  {

    when(database.salaryDeposit(1L, "Jasmine", 10000.00)).thenReturn(true)

    databaseServiceActor ! (1L, "Jasmine", 10000.00)

    expectMsg("Salary deposited successfully")

  }

  test("Testing DatabaseServiceActor for returning list of billerCategory for a given account number")
  {

    when(database.getLinkedBiller).thenReturn(linkedBiller)

    databaseServiceActor ! (1L)

    expectMsgPF() {

      case billerCategoryList: Seq[Category.Value] => assert(billerCategoryList(0) == Category.car &&
        billerCategoryList(1) == Category.internet)

    }

  }

  test("Testing DatabaseServiceActor for paying bill using account number")
  {

    when(database.payBill(1L, Category.car)).thenReturn(true)

    databaseServiceActor ! (1L, Category.car)

    expectMsg("Bill Paid Successfully")

  }

}