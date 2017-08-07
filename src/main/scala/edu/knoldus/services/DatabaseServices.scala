package edu.knoldus.services

import akka.actor.{Actor, ActorLogging, Props}
import edu.knoldus.models.Category
import edu.knoldus.{CustomerAccount, Database}


class DatabaseService extends Actor with ActorLogging with Database{

  override def receive: Receive = {

    case listOfInformation: List[String] =>
      log.info("Checking if username already exists ")
      if(!getUserAccount.contains(listOfInformation(3))) {
        val customerAccount = CustomerAccount(listOfInformation)
        addUserAccount(customerAccount.userName,CustomerAccount(listOfInformation))
        sender() ! (customerAccount.userName, "Account created successfully!!")
      }
      else{
        sender() ! (listOfInformation(3),"Username already exists!!")
      }

    case (accountNumber: Long, name: String, category: Category.Value) =>

      val listOfBillers = getLinkedBiller.getOrElse(accountNumber, Nil)
      if(listOfBillers.isEmpty|| (!listOfBillers.exists(_.category == category))){

        linkBiller(accountNumber,name,category)
        sender() ! ("Linked to biller successfully!!")
      }
      else {
        sender() ! "Already linked to the biller!!"
      }
    case (accountNumber: Long, name: String,salary: Double) =>

      salaryDeposit(accountNumber,name,salary)

    case (accountNumber: Long, billToBePaid: Double) =>
      payBill(accountNumber,billToBePaid)

    case username: String => sender ! getUserAccount(username).accountNumber
    case accountNo: Long => sender() ! getLinkedBiller.getOrElse(accountNo, Nil).map(_.category)

    case _ =>
      log.error("Invalid information")
  }


}

object DatabaseService{
  def props(): Props = Props[DatabaseService]

}
