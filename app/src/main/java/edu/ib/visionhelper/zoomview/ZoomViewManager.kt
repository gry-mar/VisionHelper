package edu.ib.visionhelper.zoomview

import android.content.Context
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.TextSizePreferencesManager

class ZoomViewManager(var context: Context) {
    private var preferences: TextSizePreferencesManager? = null

    var textSize: Float

    init {
        preferences = TextSizePreferencesManager(context)
        textSize = preferences!!.textSize
    }

    fun zoomIn(): Float {
        textSize = textSize.plus(2)

        return textSize
    }

    fun zoomOut(): Float {
        textSize = textSize.minus(2)
        return textSize
    }

    fun confirmTextSize() {
        preferences!!.textSize = textSize
    }

}