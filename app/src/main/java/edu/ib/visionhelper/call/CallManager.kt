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
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.FileManager
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager

/**
 * Manager class to handle CallActivity logic
 */
class CallManager(
    context: Context, activity: CallActivity,
    private val lifecycleOwner: LifecycleOwner
) {

    private var speechManager: SpeechManager = SpeechManager(context)
    private var speechManagerSecond: SpeechManager = SpeechManager(context)
    var speechManagerThird: SpeechManager = SpeechManager(context)
    private var speechManager4: SpeechManager = SpeechManager(context)
    private var speechManager5: SpeechManager = SpeechManager(context)
    private var speechManager6: SpeechManager = SpeechManager(context)
    var speechManager7: SpeechManager = SpeechManager(context)
    private var speechRecognizerManager: SpeechRecognizerManager = SpeechRecognizerManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    private var activityContext = context
    var addContactStarted = MutableLiveData(false)
        private set
    var removeContactStarted = MutableLiveData(false)
        private set
    var updateUI = MutableLiveData(false)
        private set
    var updateUIDelete = MutableLiveData(false)
        private set
    var longPressActivated = MutableLiveData(true)
        private set
    var addContactNumber = MutableLiveData(false)
        private set
    private var fileManager: FileManager = FileManager()
    private var temporaryContact = CallListElement("", 0)
    private lateinit var callButton: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var removeButton: ImageButton

    init {
        preferences = PreferencesManager(context)
        addContactStarted.value = false
        addContactNumber.value = false
        longPressActivated.value = true
        removeContactStarted.value = false
        updateUIDelete.value = false

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
        arrayList = fileManager.readFile(activityContext) as ArrayList<CallListElement>
        fileManager.readFile(activityContext)
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
        returnedText = text.replace(" ", "")
        val temporaryContactNameList: MutableList<String> = mutableListOf()
        for (callElement in arrayList) {
            temporaryContactNameList += callElement.contactName
        }
        if (addContactStarted.value == true) {
            longPressActivated.value = false
            if (!temporaryContactNameList.contains(returnedText)) {
                speak(
                    activityContext.getString(R.string.contact_name_is) + returnedText +
                            "Teraz naciśnij przycisk słuchawki aby otworzyć klawiaturę i dodać numer. " +
                            "Po wprowadzeniu numeru naciśnij zielony przycisk Aby zatwierdzić wybór",
                    4
                )
                speechManager4.finished.observe(lifecycleOwner, {
                    if (it) {
                        temporaryContact.contactName = returnedText
                        addContactNumber.value = true
                        print("Is clickable" + callButton.isClickable)
                        longPressActivated.value = false
                        callButton.isClickable = true
                        speechManager.finished.value = false
                        removeButton.isClickable = true
                    }
                })
                return
            } else {
                speak("Taki kontakt już istnieje przytrzymaj ponownie aby dodać kontakt", 5)
                speechManager5.finished.observe(lifecycleOwner, {
                    if (it) {
                        longPressActivated.value = true
                        callButton.isClickable = false
                    }
                })
                return
            }

        } else if (removeContactStarted.value == true) {
            if (temporaryContactNameList.contains(returnedText)) {
                println(returnedText)
                fileManager.removeLineContains(activityContext, returnedText)
                speak("Usuwam kontakt o nazwie $returnedText", 7)
                speechManager7.finished.observe(lifecycleOwner, {
                    if (it) {
                        removeContactStarted.value = false
                        updateUIDelete.value = false
                        addButton.isClickable = true

                    }
                })
            } else {
                speak("Nie ma kontaktu o nazwie $returnedText. Jeśli chcesz usunąć kontakt rozpocznij usuwanie ponownie", 2)
                removeContactStarted.value = false
                updateUIDelete.value = false
                addButton.isClickable = true
            }
        } else if (removeContactStarted.value == false && addContactStarted.value == false) {

            if (temporaryContactNameList.contains(returnedText)) {
                speak(activityContext.getString(R.string.call_to) + returnedText, 2)
                speechManagerSecond.finished.observe(lifecycleOwner, {
                    if (it) {
                        call()
                    }
                })
            } else {
                speak("Nie ma kontaktu o nazwie $returnedText", 2)
            }
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
    fun speak(text: String, instanceId: Int) {
        when (instanceId) {
            1 -> {
                speechManager.speakWithObservable(text)
            }
            2 -> {
                speechManagerSecond.speakWithObservable(text)
            }
            3 -> {
                speechManagerThird.speakWithObservable(text)
            }
            4 -> {
                speechManager4.speakWithObservable(text)
            }
            5 -> {
                speechManager5.speakWithObservable(text)
            }
            6 -> {
                speechManager6.speakWithObservable(text)
            }
            7 -> {
                speechManager7.speakWithObservable(text)
            }
        }
    }


    /**
     * Function that opens call intent if speechManager finished speaking
     */
    private fun call() {
        println(returnedText)
        arrayList.forEach { element ->
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

    /**
     * Function to handle the beginning of contact add
     * also updates image button icons and colors
     */
    fun handleContactAdd(
        callButton: ImageButton,
        addButton: ImageButton,
        removeButton: ImageButton
    ) {
        this.callButton = callButton
        this.addButton = addButton
        this.removeButton = removeButton
        removeButton.isClickable = false
        longPressActivated.value = false
        callButton.isClickable = false
        if (addContactStarted.value == false) {
            speak(
                "Aby dodać nazwę kontaktu przytrzymaj przycisk ze słuchawką i wypowiedz jego nazwę.",
                1
            )
            speechManager.finished.observe(lifecycleOwner, {
                if (it) {
                    addContactStarted.value = true
                    callButton.isClickable = false
                    updateUI.value = true
                    longPressActivated.value = true
                    speechManager.finished.value = false
                }
            })
        } else {
            speak("Wyjście z dodawania kontaktu", 2)
            speechManagerSecond.finished.observe(lifecycleOwner, {
                if (it) {
                    addContactStarted.value = false
                    callButton.isClickable = false
                    updateUI.value = false
                    longPressActivated.value = true
                    speechManagerSecond.finished.value = false
                    removeButton.isClickable = true

                }
            })
        }
    }

    fun handleCheckNumber(contactNumber: String, numericKeyboard: View) {
        var contact = contactNumber
        if (contact.length != 9) {
            speak("Numer kontaktu za krótki. Wprowadź ponownie", 1)
            speechManager.finished.observe(lifecycleOwner, {
                if (it) {
                    contact = ""
                }
            })
        } else {
            temporaryContact.contactNumber = contact.toLong()
            fileManager.writeToInternal(
                "${temporaryContact.contactName};${temporaryContact.contactNumber}",
                activityContext
            )
            speak(
                "Poprawnie dodano kontakt o nazwie ${temporaryContact.contactName} i numerze" +
                        " ${temporaryContact.contactNumber}", 3
            )
            speechManagerThird.finished.observe(lifecycleOwner, {
                if (it) {
                    println(contact)
                    println(temporaryContact)
                    addContactNumber.value = false
                    addContactStarted.value = false
                    numericKeyboard.visibility = View.GONE
                    longPressActivated.value = true
                    updateUI.value = false
                    speechManagerThird.finished.value = false
                }
            })
        }
    }

    fun handleRemoveContact(callButton: ImageButton, removeButton: ImageButton, addButton: ImageButton) {
        this.callButton = callButton
        this.removeButton = removeButton
        this.addButton = addButton
        longPressActivated.value = false
        callButton.isClickable = false
        if (removeContactStarted.value == false) {
            speak("Aby usunąć kontakt przytrzymaj przycisk słuchawki i wypowiedz jego nazwę.", 6)
            speechManager6.finished.observe(lifecycleOwner, {
                if (it) {
                    removeContactStarted.value = true
                    callButton.isClickable = false
                    updateUIDelete.value = true
                    longPressActivated.value = true
                    addButton.isClickable = false
                }
            })
        } else {

            speak("Wyjście z usuwania kontaktu", 2)
            speechManagerSecond.finished.observe(lifecycleOwner, {
                if (it) {
                    removeContactStarted.value = false
                    callButton.isClickable = false
                    updateUIDelete.value = false
                    longPressActivated.value = true
                    addButton.isClickable = true

                }
            })
        }
    }
}