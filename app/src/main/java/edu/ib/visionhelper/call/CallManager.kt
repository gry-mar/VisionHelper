package edu.ib.visionhelper.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.FileManager
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager

/**
 * Manager class to handle CallActivity logic
 */
class CallManager(context: Context, activity: CallActivity,
                  private val lifecycleOwner: LifecycleOwner
) {

    private var speechManager: SpeechManager = SpeechManager(context)
    private var speechManagerSecond: SpeechManager = SpeechManager(context)
    private var speechManagerThird: SpeechManager = SpeechManager(context)
    private var speechManager4: SpeechManager = SpeechManager(context)
    private var speechRecognizerManager: SpeechRecognizerManager = SpeechRecognizerManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    private var activityContext = context
    var addContactStarted = MutableLiveData<Boolean>(false)
    private set
    var updateUI = MutableLiveData<Boolean>(false)
        private set
    var longPressActivated = MutableLiveData<Boolean>(true)
        private set
    var addContactNumber = MutableLiveData<Boolean>(false)
        private set
    private var fileManager: FileManager = FileManager()
    private var temporaryContact = CallListElement("",0)
    private lateinit var callButton: ImageButton
    private lateinit var addButton: ImageButton

    init {
        preferences = PreferencesManager(context)
        addContactStarted.value = false
        addContactNumber.value = false
        longPressActivated.value = true

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

        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText = text.replace(" ","")
        if (addContactStarted.value == true){
            longPressActivated.value = false
            speak4(activityContext.getString(R.string.contact_name_is)+returnedText+
                    "Teraz naciśnij przycisk słuchawki aby otworzyć klawiaturę i dodać numer. " +
                    "Po wprowadzeniu numeru naciśnij zielony przycisk Aby zatwierdzić wybór")
            speechManager4.finished.observe(lifecycleOwner, Observer {
                if(it){
                    temporaryContact.contactName = returnedText
                    addContactNumber.value = true
                    print("Is clickable"+callButton.isClickable)
                   // addContactNumber.postValue(true)
                   // addContactStarted.postValue(false)
                    longPressActivated.value = false
                    callButton.isClickable = true
                    speechManager.finished.value = false
                }
            })
            return
        }
            else {
                speakSecond(activityContext.getString(R.string.call_to) + returnedText)
                speechManagerSecond.finished.observe(lifecycleOwner, Observer {
                    if(it){
                        call()
                    }
                })

            }
    }


    /**
     * Function to listen to input voice
     */
    fun listen() {
        if (addContactNumber.value == false) {
            speech.startListening(recognizerIntent)
        }
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
        speechManager.speakWithObservable(text)
    }

    fun speakSecond(text: String){
        speechManagerSecond.speakWithObservable(text)
    }

    fun speakThird(text: String){
        speechManagerThird.speakWithObservable(text)
    }
    fun speak4(text: String){
        speechManager4.speakWithObservable(text)
    }

    /**
     * Function that opens call intent if speechManager finished speaking
     */
    private fun call() {
        println(returnedText)
       // if (speechManager.isFinishedSpeaking == 1) {

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
      // }
    }

    /**
     * Function to handle the beginning of contact add
     * also updates image button icons and colors
     */
    fun handleContactAdd(callButton: ImageButton, addButton: ImageButton ){
        this.callButton = callButton
        this.addButton = addButton
        longPressActivated.value = false
       callButton.isClickable = false
        //callButton.isLongClickable = false
        if(addContactStarted.value == false){
            speak("Aby dodać nazwę kontaktu przytrzymaj przycisk ze słuchawką i wypowiedz jego nazwę.")
            speechManager.finished.observe( lifecycleOwner, Observer {
                if(it){
                    println("Status: "+it)
                    addContactStarted.value = true
                    //addContactStarted.postValue(true)
                   // callButton.isLongClickable = true
                  callButton.isClickable= false
                   updateUI.value = true
                    longPressActivated.value = true
                    speechManager.finished.value = false

                   // updateUI.postValue(true)

                }
            })

        }
        else{

            speakSecond("Wyjście")
            speechManagerSecond.finished.observe( lifecycleOwner, Observer {
                if(it){
                    addContactStarted.value = false
                    //addContactStarted.postValue(false)
                    callButton.isClickable = false
                    //callButton.isLongClickable = true
                    updateUI.value = false
                  // updateUI.postValue(false)
                    longPressActivated.value = true
                    speechManagerSecond.finished.value = false


                }
            })
        }

    }
    fun handleCheckNumber(contactNumber: String, numericKeyboard: View){
        var contactNumber = contactNumber
        if(contactNumber.length != 9){
            speak("Numer kontaktu za krótki. Wprowadź ponownie")
            speechManager.finished.observe( lifecycleOwner, Observer {
                if(it) {
                    contactNumber = ""
                }})
        }
        else {
            temporaryContact.contactNumber = contactNumber.toLong()
            speakThird("Poprawnie dodano kontakt")
            speechManagerThird.finished.observe( lifecycleOwner, Observer {
                if(it){

                    println(contactNumber)
                    println(temporaryContact)
                    addContactNumber.setValue(false)
                    addContactStarted.value = false
                   // addContactNumber.postValue(false)
                    numericKeyboard.visibility = View.GONE
                    longPressActivated.value = true
                    updateUI.value = false
                    //updateUI.postValue(false)
                    speechManagerThird.finished.value = false

                }
            })

        }
    }
}