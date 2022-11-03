package edu.ib.visionhelper.manager

import android.util.Log
import androidx.core.text.isDigitsOnly

class CalculatorManager {

    private var prevNum: Int = 0
    private var nextNum: Int = 0

    fun sum(prevNum : Int, nextNum : Int): Int {
        return prevNum + nextNum
    }

    fun substract(prevNum : Int, nextNum : Int): Int {
        return prevNum - nextNum
    }

   fun divide(prevNum : Int, nextNum : Int): Int {
        return try {
            prevNum / nextNum
        } catch (e: ArithmeticException) {
            Log.d("ArithmeticFail", "Failed to divide - you cannot divide by zero.")
        }
    }

    fun multiply(prevNum : Int, nextNum : Int): Int {
        return prevNum * nextNum;
    }

    fun textSeparator(str : String): MutableList<String> {
        var delimiter = " "
        val parts = str.split(delimiter)
        val array = parts.toMutableList()
        return array
    }

    fun textAnalizer(arrayString: MutableList<String>): Int {
        var numberToHold = 0
        var numberFirst = 0
        var numberSecond = 0
        var sign = "0"
        var finalNumber = 0

        numberFirst = Integer.parseInt(arrayString.get(0))
        sign = arrayString.get(1)
        if (arrayString.get(2).isDigitsOnly()) {
            numberSecond = Integer.parseInt(arrayString.get(2))
        } else {
            numberSecond = Integer.parseInt(arrayString.get(3))
        }

        if (sign.equals("x") or sign.equals("razy") or sign.equals("X")) {
            finalNumber = multiply(numberFirst, numberSecond)
            println(finalNumber)
        } else if (sign.equals("plus") or sign.equals("dodaj") or sign.equals("+") or sign.equals("dodać")) {
            finalNumber = sum(numberFirst, numberSecond)
            println(finalNumber)
        } else if (sign.equals("minus") or sign.equals("odejmij") or sign.equals("-") or sign.equals("odjąć")) {
            finalNumber = substract(numberFirst, numberSecond)
            println(finalNumber)
        } else if (sign.equals("/:") or sign.equals("\\:") or sign.equals("podziel") or sign.equals("podzielić")) {
            finalNumber = divide(numberFirst, numberSecond)
            println(finalNumber)
        }

        return finalNumber

            /*for (item in arrayString)
            if (item.isDigitsOnly()){
                numberToHold = item
                numberFirst = item
            }*/
        }
}
