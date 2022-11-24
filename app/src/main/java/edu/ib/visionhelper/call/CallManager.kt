package edu.ib.visionhelper.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.FileManager
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager

/**
 * Manager class to handle CallActivity logic
 */
class CallManager(context: Context, activity: CallActivity) {

    private var speechManager: SpeechManager = SpeechManager(context)
    private var speechRecognizerManager: SpeechRecognizerManager = SpeechRecognizerManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    private var activityContext = context
    private var addContactStarted: Boolean = false
    private var addContactNumber: Boolean = false
    private var fileManager: FileManager = FileManager()
    private var temporaryContact = CallListElement("",0)
    private lateinit var callButton: ImageButton
    private lateinit var addButton: ImageButton

    init {
        preferences = PreferencesManager(context)
        addContactStarted = false
        addContactNumber = false

        TextToSpeech.OnInitListener {
            if (preferences!!.callFirstTimeLaunched == 0) {
                speechManager.speakOut(context.getString(R.string.call_helper_text))
                preferences!!.callFirstTimeLaunched = 1
            }
        }

        speech = SpeechRecognizer.createSpeechRecognizer(context)
        Log.i(
            "logTag", "isRecognitionAvailable: " +
                    SpeechRecognizer.isRecognitionAvailable(context)
        )

        if (!speechRecognizerManager.isSpeechRecognizerAvailable()) {
            Toast.makeText(
                activityContext,
                activityContext.getString(R.string.install_google_app), Toast.LENGTH_LONG
            ).show()
        }

        speech.setRecognitionListener(activity)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        arrayList.add(CallListElement("Mama", 666666666))
        arrayList.add(CallListElement("Dziadek", 789563124))
        arrayList.add(CallListElement("Lekarz", 609391014))
        arrayList.add(CallListElement("Lekarz1", 609391014))
        arrayList.add(CallListElement("Lekarz2", 609391014))
        adapter = CallListAdapter(context, arrayList, this)
    }

    /**
     * Handles results of input speech: result text given to call contact or when contact add
     * (name and then number)
     */
    fun handleResults(results: Bundle?) {
        speechManager.isFinishedSpeaking = 0
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText = text.replace(" ","")
        if (addContactStarted && !addContactNumber){
            speak(activityContext.getString(R.string.contact_name_is)+returnedText+
                    activityContext.getString(R.string.press_again))
            temporaryContact.contactName = returnedText
            addContactNumber = true
            addContactStarted = false
            while (speechManager.isFinishedSpeaking != 1) {
                //wait for speech manager to finish speaking
            }
            return
        }
        if (addContactNumber && !addContactStarted){
            if (returnedText.isDigitsOnly()){
                if(returnedText.length==9){
                    temporaryContact.contactNumber = returnedText.toInt()
                    speak(activityContext.getString(R.string.contact_add_success))
                    while (speechManager.isFinishedSpeaking != 1) {
                        //wait for speech manager to finish speaking
                    }
                    callButton.setBackgroundResource(R.drawable.shape_blue_circle)
                    addButton.setImageResource(R.drawable.ic_add_note)
                    addContactNumber = false
                    addContactStarted = false
                    println(temporaryContact.toString())
                    return
                }
                else{
                    speak(activityContext.getString(R.string.contact_number_too_short))
                    while (speechManager.isFinishedSpeaking != 1) {
                        //wait for speech manager to finish speaking
                    }
                    addContactNumber = false
                    addContactStarted = true
                    return
                }
            }
            else {
                speak(activityContext.getString(R.string.contact_number_letters))
                while (speechManager.isFinishedSpeaking != 1) {
                    //wait for speech manager to finish speaking
                }
                addContactNumber = false
                addContactStarted = true
                return
            }
        }
        else {
            var temporaryNamesList = listOf<String>()
            arrayList.forEach { element ->
                temporaryNamesList += element.contactName.lowercase()
            }
            if( !temporaryNamesList.contains(returnedText)){
                speak(activityContext.getString(R.string.contact_no_contact)+ returnedText)
                while (speechManager.isFinishedSpeaking != 1) {
                    //wait for speech manager to finish speaking
                }
                return
            }else {
                speechManager.speakOut(activityContext.getString(R.string.call_to) + returnedText)
                while(speechManager.isFinishedSpeaking != 1){
                    // wait
                }
                call()
            }
        }
    }



    /**
     * Function to listen to input voice
     */
    fun listen() {
        speech.startListening(recognizerIntent)
    }

    /**
     * Calls speech manager function to stop speaking
     */
    fun stopSpeaking() {
        speechManager.stopSpeaking()
    }

    /**
     * Calls speechManager speakOut method with given text
     */
    fun speak(text: String) {
        speechManager.speakOut(text)
    }



    /**
     * Function that opens call intent if speechManager finished speaking
     */
    private fun call() {
        println(returnedText)
        if (speechManager.isFinishedSpeaking == 1) {

            arrayList.forEach{ element ->
                println(element.contactNumber)
                if (element.contactName.lowercase() == returnedText) {
                    println(element.contactNumber)
                    val intent =
                        Intent(
                            Intent.ACTION_CALL, Uri.parse(
                                "tel:" +
                                        element.contactNumber
                            )
                        )
                    activityContext.startActivity(intent)
                }
            }
       }
    }

    /**
     * Function to handle the beginning of contact add
     * also updates image button icons and colors
     */
    fun handleContactAdd(callButton: ImageButton, addButton: ImageButton ){
        this.callButton = callButton
        this.addButton = addButton
        if(!addContactStarted){
            if(!addContactStarted){
                callButton.setBackgroundResource(R.drawable.shape_circle_green)
                addButton.setImageResource(R.drawable.ic_cancel)
                speak(activityContext.getString(R.string.contact_add_contact_start))
                while (speechManager.isFinishedSpeaking != 1){
                    //wait for speech manager to finish speaking
                }
                addContactStarted = true
            }

        }
        else{
            //speak("Wyjście")
            callButton.setBackgroundResource(R.drawable.shape_blue_circle)
            addButton.setImageResource(R.drawable.ic_add_note)
            addContactStarted = false
        }

    }
}