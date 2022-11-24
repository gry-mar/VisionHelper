package edu.ib.visionhelper.calculator

import android.util.Log
import android.widget.Toast
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
        return (prevNum / nextNum)
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

    fun minusValuesFixDelimiter(arrayString: MutableList<String>, delimiter: String): MutableList<String> {
        val stringToSplit = arrayString.joinToString(prefix = "", postfix = "", separator = "")
        val parts = stringToSplit.split(delimiter)
        arrayString.clear()
        val arrayString = parts.toMutableList()
        arrayString.add(1, "-")
        return arrayString
    }

    fun minusValuesFix(arrayString: MutableList<String>): MutableList<String> {
        var returnArray = arrayString
        if (arrayString.get(0).contains("-"))
            returnArray = minusValuesFixDelimiter(arrayString, "-")
        else if (arrayString.get(0).contains("minus"))
            returnArray = minusValuesFixDelimiter(arrayString, "minus")
        else if (arrayString.get(0).contains("odjąć"))
            returnArray = minusValuesFixDelimiter(arrayString, "odjąć")
        else if (arrayString.get(0).contains("odejmij"))
            returnArray = minusValuesFixDelimiter(arrayString, "odejmij")
        else if (arrayString.get(1).contains("-"))
            returnArray = minusValuesFixDelimiter(arrayString, "-")
        else if (arrayString.get(1).contains("minus"))
            returnArray = minusValuesFixDelimiter(arrayString, "minus")
        else if (arrayString.get(1).contains("odjąć"))
            returnArray = minusValuesFixDelimiter(arrayString, "odjąć")
        else if (arrayString.get(1).contains("odejmij"))
            returnArray = minusValuesFixDelimiter(arrayString, "odejmij")

        return returnArray
    }

    fun textOrganizer(arrayString: MutableList<String>): String {
        var sign = "0"

        sign = arrayString.get(1)

        if (sign.equals("x") or sign.equals("razy") or sign.equals("X")) {
            arrayString.set(1, "x")
        } else if (sign.equals("plus") or sign.equals("dodaj") or sign.equals("+") or sign.equals("dodać")) {
            arrayString.set(1, "+")
        } else if (sign.equals("minus") or sign.equals("odejmij") or sign.equals("-") or sign.equals("odjąć")) {
            arrayString.set(1, "-")
        } else if (sign.equals("/:") or sign.equals("\\:") or sign.equals("podziel") or sign.equals("podzielić") or sign.equals("na")) {
            arrayString.set(1, ":")
        }

        val string = arrayString.joinToString(prefix = "", postfix = "", separator = " ")

        return string
    }

    fun textToDigitsChanger(arrayString: MutableList<String>): MutableList<String>{

        var arrayStringFinal = minusValuesFix(arrayString)

        when(arrayStringFinal.get(0)){
            "zero" -> arrayStringFinal.set(0, "0")
            "jeden" -> arrayStringFinal.set(0, "1")
            "dwa" -> arrayStringFinal.set(0, "2")
            "trzy" -> arrayStringFinal.set(0, "3")
            "cztery" -> arrayStringFinal.set(0, "4")
            "pięć" -> arrayStringFinal.set(0, "5")
            "sześć" -> arrayStringFinal.set(0, "6")
            "siedem" -> arrayStringFinal.set(0, "7")
            "osiem" -> arrayStringFinal.set(0, "8")
            "dziewięć" -> arrayStringFinal.set(0, "9")

            else -> println(":)")
        }

        when(arrayStringFinal.get(2)){
            "zero" -> arrayStringFinal.set(2, "0")
            "jeden" -> arrayStringFinal.set(2, "1")
            "dwa" -> arrayStringFinal.set(2, "2")
            "trzy" -> arrayStringFinal.set(2, "3")
            "cztery" -> arrayStringFinal.set(2, "4")
            "pięć" -> arrayStringFinal.set(2, "5")
            "sześć" -> arrayStringFinal.set(2, "6")
            "siedem" -> arrayStringFinal.set(2, "7")
            "osiem" -> arrayStringFinal.set(2, "8")
            "dziewięć" -> arrayString.set(2, "9")

            else -> println(":)")
        }

        return arrayStringFinal
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
        } else if (sign.equals("plus") or sign.equals("dodaj") or sign.equals("+") or sign.equals("dodać")) {
            finalNumber = sum(numberFirst, numberSecond)
        } else if (sign.equals("minus") or sign.equals("odejmij") or sign.equals("-") or sign.equals("odjąć")) {
            finalNumber = substract(numberFirst, numberSecond)
        } else if (sign.equals("/:") or sign.equals("\\:") or sign.equals("podziel") or sign.equals("podzielić") or sign.equals("na")) {
            finalNumber = divide(numberFirst, numberSecond)
        }

        return finalNumber

    }

}