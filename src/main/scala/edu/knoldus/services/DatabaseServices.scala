package edu.knoldus.services

import akka.actor.{Actor, ActorLogging, Props}
import edu.knoldus.{CustomerAccount, Database}
import edu.knoldus.models.{Category}


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
    case username: String => sender ! getUserAccount(username).accountNumber

    case (accountNumber: Long, name: String,salary: Double) =>

      salaryDeposit(accountNumber,name,salary)
      sender() ! ("Salary deposited successfully")

    case accountNo: Long => sender() ! getLinkedBiller.getOrElse(accountNo, Nil).map(_.category)

    case (accountNumber: Long, category: Category.Value, billToBePaid: Double) =>
      val resultOfBillPaid = payBill(accountNumber,category,billToBePaid)
      log.info("Received return as " + resultOfBillPaid + " and sending it to sender " + sender())
      sender() ! resultOfBillPaid

  }


}

object DatabaseService{
  def props(): Props = Props[DatabaseService]

}
