package edu.ib.visionhelper.manager

import android.content.Context

class ZoomViewManager(var context: Context) {
    private var preferences: PreferencesManager? = null

    var textSize: Float

    init {
        preferences = PreferencesManager(context)
        textSize = preferences!!.textSizeFromZoomView
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
        preferences!!.textSizeFromZoomView = textSize
    }

}