package edu.knoldus

case class CustomerAccount(accountNumber: Long, holderName: String, address: String, userName: String, initialAmount: Double)

object CustomerAccount {

  def apply(listOfInformation: List[String]):CustomerAccount ={

    val ZERO = 0
    val ONE = 1
    val TWO = 2
    val THREE = 3
    val FOUR = 4

    CustomerAccount(listOfInformation(ZERO).toLong, listOfInformation(ONE), listOfInformation(TWO),
      listOfInformation(THREE), listOfInformation(FOUR).toDouble)
  }

}
