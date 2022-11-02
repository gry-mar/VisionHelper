package edu.ib.visionhelper.manager

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager (context: Context){

    private val APP_PREFERENCES_NAME = "visionHelperPreferences"
    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var mainFirstTimeLaunched: Int
        get() = preferences.getInt("mainLaunch", 0)
        set(value) = preferences.edit().putInt("mainLaunch", value).apply()

    var cameraFirstTimeLaunched: Int
        get() = preferences.getInt("cameraLaunch", 0)
        set(value) = preferences.edit().putInt("cameraLaunch", value).apply()

    var callFirstTimeLaunched: Int
        get() = preferences.getInt("callLaunch", 0)
        set(value) = preferences.edit().putInt("callLaunch", value).apply()

    var notesFirstTimeLaunched: Int
        get() = preferences.getInt("notesLaunch", 0)
        set(value) = preferences.edit().putInt("notesLaunch", value).apply()

    var calculatorFirstTimeLaunched: Int
        get() = preferences.getInt("calculatorLaunch", 0)
        set(value) = preferences.edit().putInt("calculatorLaunch", value).apply()

    var zoomFirstTimeLaunched: Int
        get() = preferences.getInt("zoomLaunch", 0)
        set(value) = preferences.edit().putInt("zoomLaunch", value).apply()

    var textSizeFromZoomView: Float
        get() = preferences.getFloat("textSize", 34F)
        set(value) = preferences.edit().putFloat("textSize", value).apply()

}



