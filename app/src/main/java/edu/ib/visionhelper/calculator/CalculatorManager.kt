package edu.ib.visionhelper.calculator

import androidx.core.text.isDigitsOnly


class CalculatorManager {

    /**
     * Function that adds tweo numbers
     */
    fun sum(prevNum: Int, nextNum: Int): Int {
        return prevNum + nextNum
    }

    /**
     * Function that substracts two numbers
     */
    fun substract(prevNum: Int, nextNum: Int): Int {
        return prevNum - nextNum
    }

    /**
     * Function that divides two numbers
     */
    fun divide(prevNum: Int, nextNum: Int): Int {
        return (prevNum / nextNum)
    }

    /**
     * Function that multiplies two numbers
     */
    fun multiply(prevNum: Int, nextNum: Int): Int {
        return prevNum * nextNum;
    }

    /**
     * Function that separates every item in a text and makes an array
     */
    fun textSeparator(str: String): MutableList<String> {
        var delimiter = " "
        val parts = str.split(delimiter)
        val array = parts.toMutableList()
        return array
    }

    /**
     * Function that changes signs from recognized text to unified values
     */
    fun textOrganizer(arrayString: MutableList<String>): String {
        var sign = "0"

        sign = arrayString.get(1)

        if (sign.equals("x") or sign.equals("razy")) {
            arrayString.set(1, "x")
        } else if (sign.equals("dodaj") or sign.equals("Dodaj") or sign.equals("+") or sign.equals("plus"))
        {
            arrayString.set(1, "+")
        } else if (sign.equals("minus") or sign.equals("odejmij") or sign.equals("-") or sign.equals("odjąć"))
        {
            arrayString.set(1, "-")
        } else if (sign.equals("/") or sign.equals("podziel") or sign.equals("Podziel") or sign.equals(":") or sign.equals("podzielić")
            or sign.equals("na") or sign.equals("przez") or sign.equals("dzielone") or sign.equals("dzielić"))
        {
            if (arrayString.get(2).equals("przez") || arrayString.get(2).equals("na"))
                arrayString.removeAt(2)
            arrayString.set(1, ":")

        }

        val string = arrayString.joinToString(prefix = "", postfix = "", separator = " ")

        return string
    }

    /**
     * Function that checks every item and changes text to digit eventually
     */
    fun textToDigitsChanger(arrayString: MutableList<String>): MutableList<String> {

        for (i in 0..arrayString.size - 1)
            changeDigitsToTextAutomatic(arrayString, i)

        return arrayString
    }

    /**
     * Function that changes text to digit (just in case if a number is recognized as a text)
     */
    fun changeDigitsToTextAutomatic(arrayString: MutableList<String>, index: Int) {
        when (arrayString.get(index)) {
            "zero" -> arrayString.set(index, "0")
            "jeden" -> arrayString.set(index, "1")
            "dwa" -> arrayString.set(index, "2")
            "trzy" -> arrayString.set(index, "3")
            "cztery" -> arrayString.set(index, "4")
            "pięć" -> arrayString.set(index, "5")
            "sześć" -> arrayString.set(index, "6")
            "siedem" -> arrayString.set(index, "7")
            "osiem" -> arrayString.set(index, "8")
            "dziewięć" -> arrayString.set(index, "9")
            "dziesięć" -> arrayString.set(index, "10")
            "jedenaście" -> arrayString.set(index, "11")
            "dwanaście" -> arrayString.set(index, "12")
            "trzynaście" -> arrayString.set(index, "13")
            "czternaście" -> arrayString.set(index, "14")
            "piętnaście" -> arrayString.set(index, "15")
            "szesnaście" -> arrayString.set(index, "16")
            "siedemnaście" -> arrayString.set(index, "17")
            "osiemnaście" -> arrayString.set(index, "18")
            "dziewiętnaście" -> arrayString.set(index, "19")

            else -> println(":)")
        }
    }

    /**
     * Function that analizes text, recognizes proper sign and makes proper math operation
     */
    fun textAnalizer(arrayString: MutableList<String>): Int {
        var numberFirst = 0
        var numberSecond = 0
        var sign = "0"
        var finalNumber = 0

        if (arrayString.size == 3) {
            numberFirst = Integer.parseInt(arrayString.get(0))
            sign = arrayString.get(1)
            numberSecond = Integer.parseInt(arrayString.get(2))
        } else if (arrayString.size == 4) {
            numberFirst = Integer.parseInt(arrayString.get(0))
            sign = arrayString.get(1)

            if (arrayString.get(2).equals("minus"))
                numberSecond = -Integer.parseInt(arrayString.get(3))
            else
                numberSecond = Integer.parseInt(arrayString.get(3))
        }

        if (sign.equals("x") or sign.equals("razy")) {
            finalNumber = multiply(numberFirst, numberSecond)
        } else if (sign.equals("Dodaj") or sign.equals("dodaj") or sign.equals("+") or sign.equals("plus")) {
            finalNumber = sum(numberFirst, numberSecond)
        } else if (sign.equals("minus") or sign.equals("odejmij") or sign.equals("-") or sign.equals("odjąć")) {
            finalNumber = substract(numberFirst, numberSecond)
        } else if (sign.equals("/") or sign.equals("podziel") or sign.equals("Podziel") or sign.equals(":") or sign.equals("podzielić")
            or sign.equals("na") or sign.equals("przez") or sign.equals("dzielone") or sign.equals("dzielić")) {
            finalNumber = divide(numberFirst, numberSecond)
        }
        return finalNumber
    }

    /**
     * Function that checks if first item of an array is a digit
     */
    fun checkIfDigit(arrayString: MutableList<String>): Boolean {
        if (!arrayString.get(0).isDigitsOnly())
            return true
        else
            return false
    }

}