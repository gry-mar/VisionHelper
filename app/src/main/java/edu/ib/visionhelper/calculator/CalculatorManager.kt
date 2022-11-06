package edu.ib.visionhelper.calculator

import android.util.Log

class CalculatorManager {

    private var prevNum: Int = 0
    private var nextNum: Int = 0

    private fun sum(): Int {
        return prevNum + nextNum
    }

    private fun subtract(): Int {
        return prevNum - nextNum
    }

    private fun divide(): Int {
        return try {
            prevNum / nextNum
        } catch (e: ArithmeticException) {
            Log.d("ArithmeticFail", "Failed to divide - you cannot divide by zero.")
        }
    }

    private fun multiply(): Int {
        return prevNum * nextNum;
    }

}