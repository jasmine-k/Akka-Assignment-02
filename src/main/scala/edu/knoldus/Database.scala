package edu.knoldus

import java.text.SimpleDateFormat
import java.util.Calendar

import edu.knoldus.models.{Category, LinkedBiller}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

trait Database {

  private val userAccount: mutable.Map[String, CustomerAccount] = Map(
    "jas1" -> CustomerAccount(1L,"jasmine","New Delhi","jas",2000.0),
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
      LinkedBiller(Category.phone, "PhoneBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
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

  def payBill(accountNumber: Long, billToPay: Double): String = {

    val initialAmount = userAccount.values.filter(_.accountNumber == accountNumber).map(_.initialAmount).toList
    if (initialAmount.head > billToPay) {
      userAccount map {
        case (username, customerAccount) =>
          if (customerAccount.accountNumber == accountNumber) {
            val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
            (username, newCustomerAccount)
          }
          else {
            (username, customerAccount)
          }
      }
      "Bill successfully paid"
    }
    else {
      "Donot have enough amount in account"
    }

  }



}
