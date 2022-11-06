package edu.ib.visionhelper.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.speech.RecognitionService
import android.speech.SpeechRecognizer


class SpeechRecognizerManager(context: Context) {

    var isSpeechRecognizerServiceAvailable: Boolean? = null
    private var activityContext = context
    private var GOOGLE_RECOGNITION_SERVICE_NAME =
        "com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"

    fun isSpeechRecognizerAvailable(): Boolean {
        if (isSpeechRecognizerServiceAvailable == null) {
            val isRecognitionAvailable =
                activityContext.packageManager != null && SpeechRecognizer.isRecognitionAvailable(
                    activityContext
                )
            if (isRecognitionAvailable) {
                val connection: ServiceConnection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName, service: IBinder) {}
                    override fun onServiceDisconnected(name: ComponentName) {}
                }
                val serviceIntent = Intent(RecognitionService.SERVICE_INTERFACE)
                val recognizerServiceComponent =
                    ComponentName.unflattenFromString(GOOGLE_RECOGNITION_SERVICE_NAME)
                        ?: return false
                serviceIntent.component = recognizerServiceComponent
                val isServiceAvailableToBind: Boolean =
                    activityContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
                if (isServiceAvailableToBind) {
                    activityContext.unbindService(connection)
                }
                isSpeechRecognizerServiceAvailable = isServiceAvailableToBind
            } else {
                isSpeechRecognizerServiceAvailable = false
            }
        }
        return isSpeechRecognizerServiceAvailable as Boolean
    }

}