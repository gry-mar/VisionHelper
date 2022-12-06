package edu.ib.visionhelper.zoomview

import android.content.Context
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.TextSizePreferencesManager

/**
 * Class to handle logic for ZoomActivity
 */
class ZoomViewManager(var context: Context) {
    private var preferences: TextSizePreferencesManager? = null

    var textSize: Float

    init {
        preferences = TextSizePreferencesManager(context)
        textSize = preferences!!.textSize
    }

    /**
     * Method to make the default text size of the app bigger by 2pt
     */
    fun zoomIn(): Float {
        textSize = textSize.plus(2)

        return textSize
    }

    /**
     * Method to make the default text size of the app smaller by 2pt
     */
    fun zoomOut(): Float {
        textSize = textSize.minus(2)
        return textSize
    }

    /**
     * Method that saves the set text size in current activity and whole application
     */
    fun confirmTextSize() {
        preferences!!.textSize = textSize
    }

}