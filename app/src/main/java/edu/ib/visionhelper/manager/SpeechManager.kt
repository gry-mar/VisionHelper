package edu.ib.visionhelper.manager

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import edu.ib.visionhelper.MainActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.calculator.CalculatorActivity
import edu.ib.visionhelper.call.CallActivity
import edu.ib.visionhelper.camera.CameraActivity
import edu.ib.visionhelper.notes.NotesActivity
import edu.ib.visionhelper.zoomview.ZoomViewActivity
import java.util.*
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Class that manages TextToSpeech
 */
class SpeechManager(var context: Context) : TextToSpeech.OnInitListener {
    private var textToSpeech: TextToSpeech
    private var preferences: PreferencesManager? = null
    var isFinishedSpeaking: Int = 0
    var finished = MutableLiveData<Boolean>(false)
        private set
    init {
        preferences = PreferencesManager(context)
        textToSpeech = TextToSpeech(context, this)
        finished.setValue(false)
        finished.postValue(false)
    }


    fun stopSpeaking() {
        textToSpeech.stop()
    }

    fun shutdownSpeaking() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    /**
     * Function that reads given text
     * @param text - text to be read
     */
   fun speakOut(text: String) {
        finished.value = false
        textToSpeech.setSpeechRate(1.1f)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        textToSpeech.setOnUtteranceProgressListener(object :
            UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.i("TextToSpeech", "On Start")
                    isFinishedSpeaking = 1
                MainScope().launch {
                    finished.setValue(false)
                }
            }

            override fun onDone(utteranceId: String) {
                    isFinishedSpeaking = 1
                MainScope().launch {
                    finished.setValue(true)
                }

                Log.i("TextToSpeech", "On Done $isFinishedSpeaking")
            }

            override fun onError(utteranceId: String) {

            }
        })

    }

    /**
     * Function that reads given text with TTS. It is variant with MutableLiveData set
     * @param text - text to be spoken
     */
    fun speakWithObservable(text: String){
        finished.value = false
        textToSpeech.setSpeechRate(1.1f)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        textToSpeech.setOnUtteranceProgressListener(object :
            UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                Log.i("TextToSpeech", "On Start")
                isFinishedSpeaking = 0
                MainScope().launch {
                    finished.setValue(false)
                }
            }
            override fun onDone(utteranceId: String) {

                    isFinishedSpeaking = 1
                MainScope().launch {
                    finished.setValue(true)
                }
                Log.i("TextToSpeech", "On Done $isFinishedSpeaking")

            }
            override fun onError(utteranceId: String) {

            }
        })
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.getDefault())
            println(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                context.startActivity(installIntent)
            }

            if (preferences!!.mainFirstTimeLaunched == 0 && context is MainActivity) {
               speakOut(context.getString(R.string.main_helper_text))
                preferences!!.mainFirstTimeLaunched = 1
            } else if (preferences!!.cameraFirstTimeLaunched == 0 && context is CameraActivity) {
                speakOut(context.getString(R.string.camera_helper_text))
                preferences!!.cameraFirstTimeLaunched = 1
            } else if (preferences!!.notesFirstTimeLaunched == 0 && context is NotesActivity) {
                speakOut(context.getString(R.string.notes_helper_text))
                preferences!!.notesFirstTimeLaunched = 1
            } else if (preferences!!.zoomFirstTimeLaunched == 0 && context is ZoomViewActivity) {
                speakOut(context.getString(R.string.zoom_helper_text))
                preferences!!.zoomFirstTimeLaunched = 1
            } else if (preferences!!.callFirstTimeLaunched == 0 && context is CallActivity) {
                speakOut(context.getString(R.string.call_helper_text))
                preferences!!.callFirstTimeLaunched = 1
            } else if (preferences!!.calculatorFirstTimeLaunched == 0 && context is CalculatorActivity) {
                speakOut(context.getString(R.string.calculator_helper_text))
                preferences!!.calculatorFirstTimeLaunched = 1
            }

        }

    }


}