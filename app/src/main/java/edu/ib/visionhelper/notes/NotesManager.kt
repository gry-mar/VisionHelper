package edu.ib.visionhelper.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager

/**
 * Manager class to handle NotesActivity logic
 */
class NotesManager(context: Context, activity: NotesActivity) {

    private var speechManager: SpeechManager = SpeechManager(context)
    private var speechRecognizerManager: SpeechRecognizerManager = SpeechRecognizerManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<String> = ArrayList()
    var adapter: NotesListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    var addNoteStarted: Boolean = false
    var addNoteMessage: Boolean = false
    private var activityContext = context
    private lateinit var addButton: ImageButton
    private lateinit var playStopButton: ImageButton
    var noteTitle = ""

    init {
        preferences = PreferencesManager(activityContext)
        speech = SpeechRecognizer.createSpeechRecognizer(activityContext)
        Log.i(
            "logTag", "isRecognitionAvailable: " +
                    SpeechRecognizer.isRecognitionAvailable(activityContext)
        )

        TextToSpeech.OnInitListener {
            if (preferences!!.notesFirstTimeLaunched == 0) {
                speechManager.speakOut(context.getString(R.string.notes_helper_text))
                preferences!!.notesFirstTimeLaunched = 1
            }
        }

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
        arrayList.add("Przykładowa notatka 1")
        arrayList.add("Przykładowa notatka 2")
        arrayList.add("Przykładowa notatka 3")
        adapter = NotesListAdapter(context, arrayList, this)

    }

    fun handleResults(results: Bundle?, playStopNotesButton: ImageButton) {
        speechManager.isFinishedSpeaking = 0
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        this.playStopButton = playStopNotesButton
        println("set to green circle")
        playStopButton.setImageResource(R.drawable.ic_play)
        returnedText = text
        if (addNoteStarted){
            speak(activityContext.getString(R.string.note_title_is) + returnedText +
                    activityContext.getString(R.string.press_again_note))
            noteTitle = returnedText
            println("TITLE: " + noteTitle)
            while (speechManager.isFinishedSpeaking != 1) {
                //wait for speech manager to finish speaking
            }
            addNoteMessage = true
            addNoteStarted = false
            return
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
     * Function to handle the beginning of adding new note
     */
    fun handleAddButton(addButton: ImageButton){
        this.addButton = addButton
            if(!addNoteStarted){
                speak(activityContext.getString(R.string.notes_add_note_start))
                while (speechManager.isFinishedSpeaking != 1){
                    //wait for speech manager to finish speaking
                }
                addNoteStarted = true
            }
    }
}