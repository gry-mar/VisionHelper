package edu.ib.visionhelper.manager

import android.content.Context
import android.content.SharedPreferences

class TextSizePreferencesManager(context: Context) {
    private val APP_PREFERENCES_NAME = "textSizePreferences"
    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var textSize: Float
        get() = preferences.getFloat("textSize", 28f)
        set(value) = preferences.edit().putFloat("textSize", value).apply()
}