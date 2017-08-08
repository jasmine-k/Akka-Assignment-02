package edu.knoldus

import java.text.SimpleDateFormat
import java.util.Calendar

import edu.knoldus.actors.DatabaseService
import edu.knoldus.models.{Category, LinkedBiller}
import org.apache.log4j.Logger

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

trait Database {

  val logger = Logger.getLogger(this.getClass)
  private val userAccount: mutable.Map[String, CustomerAccount] = Map(
    "jas" -> CustomerAccount(1L,"Jasmine","New Delhi","jas",2000.0),
    "sim" -> CustomerAccount(2L,"simran","New Delhi","sim",2000.0),
    "ruby" -> CustomerAccount(3L,"ruby","New Delhi","ruby",2000.0)

  )

  def getUserAccount :mutable.Map[String, CustomerAccount] = userAccount

  def addUserAccount( userName: String, customerAccount: CustomerAccount): Unit ={
    userAccount += (userName -> customerAccount)

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

  def getLinkedBiller : mutable.Map[Long, ListBuffer[LinkedBiller]] = linkedBiller

  def linkBiller(accountNumber: Long, name: String, category: Category.Value): Unit ={
    val listOfBillers = linkedBiller.getOrElse(accountNumber, Nil)
    listOfBillers match {

      case listOfBiller: ListBuffer[LinkedBiller] =>
        if(!listOfBillers.isEmpty){
          linkedBiller(accountNumber) += LinkedBiller(accountNumber, name, category)
        }

      case Nil =>
        linkedBiller += accountNumber -> ListBuffer(LinkedBiller(accountNumber, name, category))
    }
  }

  def salaryDeposit (accountNumber: Long, name: String,salary: Double): Unit ={
    userAccount map {
      case (userName, customerAccount) =>
        if (customerAccount.accountNumber == accountNumber) {
          val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount + salary)
          (userName, newCustomerAccount)
        }
        else {
          (userName, customerAccount)
        }
    }

  }

  def payBill(accountNumber: Long, category: Category.Value, billToPay: Double): Boolean = {

    val billToPayList = linkedBiller.getOrElse(accountNumber, Nil).filter(_.category == category)
    if (billToPayList.isEmpty) {
      false
    }
    else {
      val billToPay = billToPayList.head.amount
      val initialAmountList = userAccount.values.filter(_.accountNumber == accountNumber)
      val initialAmount = initialAmountList.map(_.initialAmount).toList
      logger.info("Amount in the account is " + initialAmount.head)
      if (initialAmount.head > billToPay) {
        logger.info("If condition satisfied in payBill")
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

        logger.info("LinkedBiller map is as: " + linkedBiller)

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
        logger.info("Returning true")
        true
      }
      else {
        logger.info("Returning false")
        false
      }
    }
  }

}
