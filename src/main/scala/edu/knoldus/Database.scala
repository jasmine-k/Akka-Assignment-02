package edu.knoldus

import java.text.SimpleDateFormat
import java.util.Calendar

import edu.knoldus.actors.DatabaseServiceActor
import edu.knoldus.models.{Category, LinkedBiller}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

class Database {

  private val userAccount: mutable.Map[String, CustomerAccount] = Map(
    "jas" -> CustomerAccount(1L, "Jasmine", "New Delhi", "jas", 2000.0),
    "sim" -> CustomerAccount(2L, "simran", "New Delhi", "sim", 2000.0),
    "ruby" -> CustomerAccount(3L, "ruby", "New Delhi", "ruby", 2000.0)

  )

  def getUserAccount: mutable.Map[String, CustomerAccount] = userAccount

  def addUserAccount(userName: String, customerAccount: CustomerAccount): Boolean = {
    userAccount += (userName -> customerAccount)
    if (userAccount.contains(userName)) {
      true
    }
    else {
      false
    }

  }

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())


  private val linkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = Map(
    1L -> ListBuffer(
      LinkedBiller(Category.car, "CarBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.internet, "InternetBiller", 1L, currentDate, 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkedBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 0.00, 0, 0, 0.00),
      LinkedBiller(Category.food, "FoodBiller", 2L, currentDate, 0.00, 0, 0, 0.00)
    )
  )

  def getLinkedBiller: mutable.Map[Long, ListBuffer[LinkedBiller]] = linkedBiller

  def linkBiller(accountNumber: Long, name: String, category: Category.Value): Boolean = {
    val listOfBillers = linkedBiller.getOrElse(accountNumber, Nil)
    listOfBillers match {

      case listOfBiller: ListBuffer[LinkedBiller] =>
        if (!listOfBillers.isEmpty) {
          linkedBiller(accountNumber) += LinkedBiller(accountNumber, name, category)
        }
        true

      case Nil =>
        linkedBiller += accountNumber -> ListBuffer(LinkedBiller(accountNumber, name, category))
        true

      case _ => false
    }
  }

  def salaryDeposit(accountNumber: Long, name: String, salary: Double): Boolean = {

    val userCustomerAccountList = userAccount.values.filter(_.accountNumber == accountNumber)

    if (userCustomerAccountList.isEmpty) {
      false
    }

    else {
      val customerAccount = userCustomerAccountList.head
      val newUserAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount + salary)
      userAccount(customerAccount.userName) = newUserAccount
      true
    }

  }

  def payBill(accountNumber: Long, category: Category.Value): Boolean = {

    val billToPayList = linkedBiller.getOrElse(accountNumber, Nil).filter(_.category == category)
    if (billToPayList.isEmpty) {
      false
    }
    else {
      val billToPay = billToPayList.head.amount
      val initialAmountList = userAccount.values.filter(_.accountNumber == accountNumber)
      val initialAmount = initialAmountList.map(_.initialAmount).toList
      if (initialAmount.head > billToPay) {

        val linkedBillerCaseClass = linkedBiller(accountNumber).filter(_.category == category).head
        val dateWhilePayingBill = dateFormat.format(Calendar.getInstance().getTime())
        val newlinkedBillerCaseClass = linkedBillerCaseClass.copy(transactionDate = dateWhilePayingBill,
          amount = billToPay, totalIterations = linkedBillerCaseClass.totalIterations + 1,
          executedIterations = linkedBillerCaseClass.executedIterations + 1, paidAmount = linkedBillerCaseClass.amount + billToPay
        )

        val listOfLinkedBiller = linkedBiller(accountNumber)
        listOfLinkedBiller -= linkedBillerCaseClass
        listOfLinkedBiller += newlinkedBillerCaseClass
        linkedBiller(accountNumber) = listOfLinkedBiller

        userAccount foreach {
          case (username, customerAccount) =>
            if (customerAccount.accountNumber == accountNumber) {
              val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
              userAccount(username) = newCustomerAccount
            }
            else {
              userAccount(username) = customerAccount
            }
        }
        true
      }
      else {
        false
      }
    }
}

/*
    val billToPayList = linkedBiller.getOrElse(accountNumber, Nil).filter(_.category == category)
    if (billToPayList.isEmpty) {
      false
    }
    else {
      val billToPay = billToPayList.head.amount
      val initialAmountList = userAccount.values.filter(_.accountNumber == accountNumber)
      val initialAmount = initialAmountList.map(_.initialAmount).toList
      if (initialAmount.head > billToPay) {
        val linkedBillerCaseClass = linkedBiller(accountNumber).filter(_.category == category).head
        val dateWhilePayingBill = dateFormat.format(Calendar.getInstance().getTime())
        val newlinkedBillerCaseClass = linkedBillerCaseClass.copy(transactionDate = dateWhilePayingBill,
          amount = billToPay, totalIterations = linkedBillerCaseClass.totalIterations + 1,
          executedIterations = linkedBillerCaseClass.executedIterations + 1, paidAmount = linkedBillerCaseClass.amount + billToPay
        )

        val listOfLinkedBiller = linkedBiller(accountNumber)
        listOfLinkedBiller -= linkedBillerCaseClass
        listOfLinkedBiller += newlinkedBillerCaseClass
        linkedBiller(accountNumber) = listOfLinkedBiller


        userAccount foreach {
          case (userName, customerAccount) =>
            if (customerAccount.accountNumber == accountNumber) {
              val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
              userAccount(userName) = newCustomerAccount
            }
            else {
              userAccount(userName) = customerAccount
            }
        }
        true
      }
      else {
        false
      }
    }
  }
*/

}
