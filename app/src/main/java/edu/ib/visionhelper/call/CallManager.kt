package edu.ib.visionhelper.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

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
    private var speechManager8: SpeechManager = SpeechManager(context)
    var speechManager7: SpeechManager = SpeechManager(context)
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
    var isActionClosed = false
    private var callFilesManager: CallFilesManager = CallFilesManager()
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

        speech.setRecognitionListener(activity)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        arrayList = callFilesManager.readFile(activityContext) as ArrayList<CallListElement>
        callFilesManager.readFile(activityContext)
        adapter = CallListAdapter(context, arrayList, this)
    }

    /**
     * Handles results of input speech: result text given to call contact or when contact add
     * or name of contact to be deleted
     * (name and then number)
     */
    fun handleResults(results: Bundle?, addButton: ImageButton) {
        addButton.isClickable = false
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
                            activityContext.getString(R.string.contact_click_to_add_number),
                    4
                )
                speechManager4.finished.observe(lifecycleOwner, {
                    if (it) {
                        addButton.isClickable = true
                        temporaryContact.contactName = returnedText
                        addContactNumber.value = true
                        longPressActivated.value = false
                        if(!isActionClosed) {
                            callButton.isClickable = true
                        }
                        speechManager.finished.value = false
                        removeButton.isClickable = true
                    }
                })
                return
            } else {
                speak(activityContext.getString(R.string.contact_is_already_add), 5)
                speechManager5.finished.observe(lifecycleOwner, {
                    if (it) {
                        longPressActivated.value = true
                        callButton.isClickable = false
                        addButton.isClickable = true
                    }
                })
                return
            }

        } else if (removeContactStarted.value == true) {
            if (temporaryContactNameList.contains(returnedText)) {
                println(returnedText)
                callFilesManager.removeLineContains(activityContext, returnedText)
                speak(activityContext.getString(R.string.contact_delete) + returnedText, 7)
                speechManager7.finished.observe(lifecycleOwner, {
                    if (it) {
                        removeContactStarted.value = false
                        updateUIDelete.value = false
                        addButton.isClickable = true

                    }
                })
            } else {
                speak(activityContext.getString(R.string.contact_no_contact) +  returnedText
                        + activityContext.getString(R.string.contact_delete_again), 2)
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
                        addButton.isClickable = true
                    }
                })
            } else {
                speak(activityContext.getString(R.string.contact_no_contact) + returnedText, 2)
                addButton.isClickable = true
            }
        }
    }


    /**
     * Function that listens to input voice
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

    /** Chooses instance of Speech manager and triggers its speakOut function
     * @param text - text o to be read
     * @param instanceId - id of Speech manager instance
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
            8 -> {
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
     * @param callButton - button to manage voice input
     * @param addButton - button clicked to star contact addition
     * @param removeButton - button clicked to start removing contact process (will be grayed out during
     * this method)
     */
    fun handleContactAdd(
        callButton: ImageButton,
        addButton: ImageButton,
        removeButton: ImageButton
    ) {
        isActionClosed = false
        this.callButton = callButton
        this.addButton = addButton
        this.removeButton = removeButton
        removeButton.isClickable = false
        longPressActivated.value = false
        callButton.isClickable = false
        if (addContactStarted.value == false) {
            speak(
                activityContext.getString(R.string.contact_add_contact_start),
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
            speak(activityContext.getString(R.string.contact_exit_add), 2)
            speechManagerSecond.finished.observe(lifecycleOwner, {
                if (it) {
                    addContactStarted.value = false
                    callButton.isClickable = false
                    updateUI.value = false
                    longPressActivated.value = true
                    speechManagerSecond.finished.value = false
                    removeButton.isClickable = true
                    isActionClosed = true

                }
            })
        }
    }

    /**
     * Function that checks if given contact name has appropriate length. Adds contact to contact list.
     * @param contactNumber - contact number
     * @param numericKeyboard - layout with numeric keyboard to make it invisible
     */
    fun handleCheckNumber(contactNumber: String, numericKeyboard: View) {
        var contact = contactNumber
        if (contact.length != 9) {
            speak(activityContext.getString(R.string.contact_number_wrong_length), 1)
            speechManager.finished.observe(lifecycleOwner, {
                if (it) {
                    contact = ""
                }
            })
        } else {
            temporaryContact.contactNumber = contact.toLong()
            callFilesManager.writeToInternal(
                "${temporaryContact.contactName};${temporaryContact.contactNumber}",
                activityContext
            )
            speak(
                activityContext.getString(R.string.contact_succes_add) +
                        temporaryContact.contactName +
        activityContext.getString(R.string.contact_succes_add_number)+
                        temporaryContact.contactNumber, 3)
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

    /**
     * Function to remove existing contact by speaking its name
     * @param callButton - button that manages voice input
     * @param removeButton - button clicked to start removing process
     * @param addButton - button to ad contact (will be grayed out during removal)
     */
    fun handleRemoveContact(callButton: ImageButton, removeButton: ImageButton, addButton: ImageButton) {
        this.callButton = callButton
        isActionClosed = false
        this.removeButton = removeButton
        this.addButton = addButton
        longPressActivated.value = false
        callButton.isClickable = false
        if (removeContactStarted.value == false) {
            speak(activityContext.getString(R.string.contact_remove_start), 6)
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

            speak(activityContext.getString(R.string.contact_exit_remove), 8)
            speechManager8.finished.observe(lifecycleOwner, {
                if (it) {
                    removeContactStarted.value = false
                    callButton.isClickable = false
                    updateUIDelete.value = false
                    longPressActivated.value = true
                    addButton.isClickable = true
                    isActionClosed = true

                }
            })
        }
    }
}