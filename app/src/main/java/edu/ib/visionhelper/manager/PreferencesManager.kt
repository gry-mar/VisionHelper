package edu.ib.visionhelper.manager

import android.content.Context
import android.content.SharedPreferences
import android.icu.number.IntegerWidth

class PreferencesManager (context: Context){

    private val APP_PREFERENCES_NAME = "visionHelperPreferences"
    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var firstTimeLaunched: Int
        get() = preferences.getInt("int", 0)
        set(value) = preferences.edit().putInt("int", value).apply()
}



