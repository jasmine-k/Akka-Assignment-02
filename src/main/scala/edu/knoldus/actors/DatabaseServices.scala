package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, Props}
import edu.knoldus.models.Category
import edu.knoldus.{CustomerAccount, Database}


class DatabaseServiceActor extends Database with Actor with ActorLogging {


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

      val resultOfSalaryDeposit = salaryDeposit(accountNumber,name,salary)
      log.info("Received return as " + resultOfSalaryDeposit + " and sending it to sender " + sender())
      sender() ! ("Salary deposited successfully")

    case accountNo: Long => sender() ! getLinkedBiller.getOrElse(accountNo, Nil).map(_.category)

    case (accountNumber: Long, category: Category.Value) =>
      val resultOfBillPaid = payBill(accountNumber,category)
      log.info("Received return as " + resultOfBillPaid + " and sending it to sender " + sender())
      sender() ! "Bill Paid Successfully"

  }


}

object DatabaseServiceActor{
  def props(): Props = Props[DatabaseServiceActor]

}
