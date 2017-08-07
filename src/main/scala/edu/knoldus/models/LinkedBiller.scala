package edu.knoldus.models

import java.text.SimpleDateFormat
import java.util.Calendar

case class LinkedBiller(category: Category.Value , name: String, accountNumber: Long, transactionDate: String,
                  amount: Double, totalIterations: Integer, executedIterations: Integer, paidAmount: Double) {

}

object LinkedBiller {

  def apply(accountNumber: Long, name: String, category: Category.Value ): LinkedBiller ={

    val dateFormat = new SimpleDateFormat("d-M-y")
    val currentDate = dateFormat.format(Calendar.getInstance().getTime())

    LinkedBiller( category, name, accountNumber, currentDate, 0.00, 0, 0, 0.00)
  }

}
