import akka.actor.{ActorSystem, Props}
import edu.knoldus.actors.{AccountGeneratorActor, DatabaseService, LinkedBillerToAccountActor, SalaryDepositActor}
import edu.knoldus.models.Category
import edu.knoldus.services.{SalaryDepositService, UserAccountService}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object MainTest extends App {

  val actorSystem = ActorSystem("AccountSystemActor")
  val databaseServiceActor = actorSystem.actorOf(DatabaseService.props)
  val accountGeneratorActor = actorSystem.actorOf(AccountGeneratorActor.props(databaseServiceActor))
  val linkBillerToAccountActor = actorSystem.actorOf(LinkedBillerToAccountActor.props(databaseServiceActor))
  val salaryDepositorActor = actorSystem.actorOf(SalaryDepositActor.props(databaseServiceActor))

  val listOAccountInformaion = List(List("Jasmine", "New Delhi", "jas", "10.00"),
    List("Simran", "Noida", "Sim", "10.00"))
  val userAccountServiceObj = new UserAccountService
  val mapOfAccounts = userAccountServiceObj.createAccount(listOAccountInformaion, accountGeneratorActor)
  val resultString = userAccountServiceObj.linkBillerToAccount(1L, "FoodBiller", Category.food, linkBillerToAccountActor)

  val salaryDepositServiceObj = new SalaryDepositService
  val resultBool = salaryDepositServiceObj.salaryDeposit(2L, "Simran", 50000.00, salaryDepositorActor)

}

