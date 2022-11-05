package edu.ib.visionhelper.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import java.util.concurrent.TimeUnit

/**
 * Manager class to handle CallActivity logic
 */
class CallManager(context: Context, activity: CallActivity) {

    private var speechManager: SpeechManager = SpeechManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    private var activityContext = context

    init {
        preferences = PreferencesManager(context)
        speech = SpeechRecognizer.createSpeechRecognizer(context)
        Log.i("logTag", "isRecognitionAvailable: " +
                SpeechRecognizer.isRecognitionAvailable(context))
        speech.setRecognitionListener(activity)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        arrayList.add(CallListElement("Mama", 666666666))
        arrayList.add(CallListElement("Dziadek", 789563124))
        arrayList.add(CallListElement("Lekarz", 112444357))
        adapter = CallListAdapter(context, arrayList)
    }

    /**
     * Handles results of input speech: result text given to call contact
     */
    fun handleResults(results: Bundle?){
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText = text
        println(returnedText)
        speechManager.speakOut(activityContext.getString(R.string.call_to) + returnedText)
        while (speechManager.isFinishedSpeaking != 1){
                //wait for speech manager to finish speaking
        }
        TimeUnit.MILLISECONDS.sleep(500)
        call()
        println(speechManager.isFinishedSpeaking)
    }

    /**
     * Function to listen to input voice
     */
    fun listen(){
        speech.startListening(recognizerIntent)
    }

    /**
     * Calls speech manager function to stop speaking
     */
    fun stopSpeaking(){
        speechManager.stopSpeaking()
    }

    /**
     * Calls speechManager speakOut method with given text
     */
    fun speak(text: String){
        speechManager.speakOut(text)
    }

    /**
     * Function that opens call intent if speechManager finished speaking
     */
    private fun call() {
        if(speechManager.isFinishedSpeaking==1) {
            arrayList.forEach { element ->
                if (element.contactName.lowercase() == returnedText) {
                    val intent =
                        Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                element.contactNumber))
                    activityContext.startActivity(intent)
                }
            }
        }
    }
}