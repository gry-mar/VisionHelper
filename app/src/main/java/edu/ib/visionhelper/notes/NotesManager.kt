package edu.ib.visionhelper.notes

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager

/**
 * Manager class to handle NotesActivity logic
 */
@RequiresApi(Build.VERSION_CODES.S)
class NotesManager(context: Context, val activity: NotesActivity) {

    private var notesFilesManager: NotesFilesManager
    private var isRecordingStarted: Boolean = false
    private var notesRecorderManager: NotesRecorderManager
    var speechManager: SpeechManager = SpeechManager(context)
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
        notesFilesManager = NotesFilesManager()
        notesRecorderManager =
            NotesRecorderManager(activityContext, activity, noteTitle)
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

        val files = notesFilesManager.readFile(activityContext)

        var indexPrevious = 0
        var index = 0
        files.forEach { char: Char ->
            if(char == ','){
                arrayList.add(files.substring(indexPrevious, index))
                indexPrevious = index+1
            }
            index++
        }
        adapter = NotesListAdapter(context, arrayList, this)

    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun handlePlayStopButton(playStopNotesButton: ImageButton) {
        this.playStopButton = playStopNotesButton

        if(!addNoteMessage && !addNoteStarted) {
            if (notesRecorderManager.mMediaPlayer?.isPlaying == true) {
                notesRecorderManager.stopSound()
                playStopButton.setImageResource(R.drawable.ic_play)
            } else {
                if (adapter?.getItemSelected() != null) {
                    println("JEST SELECTED")
                    playStopButton.setImageResource(R.drawable.ic_stop)
                    notesRecorderManager.playSound(adapter?.getItemSelected()!!)
                }
            }
        }
            if (addNoteMessage && !addNoteStarted) {
                playStopButton.setImageResource(R.drawable.ic_play)
                if (isRecordingStarted) {
                    notesRecorderManager.stopRecording()
                    addNoteMessage = false
                    addNoteStarted = false
                    isRecordingStarted = false
                    notesFilesManager.writeToFile(noteTitle, activityContext)
                    val files = notesFilesManager.readFile(activityContext)

                    var indexPrevious = 0
                    var index = 0
                    files.forEach { char: Char ->
                        if (char == ',') {
                            arrayList.add(files.substring(indexPrevious, index))
                            indexPrevious = index + 1
                        }
                        index++
                    }
                    adapter = NotesListAdapter(activityContext, arrayList, this)

                    activity.finish()
                    activity.startActivity(activity.intent)
                } else {
                    notesRecorderManager =
                        NotesRecorderManager(activityContext, activity, noteTitle)
                    notesRecorderManager.startRecording()
                    playStopButton.setImageResource(R.drawable.ic_stop)
                    isRecordingStarted = true
                }
            }
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
        playStopButton.setImageResource(R.drawable.ic_play)
        returnedText = text
        if (addNoteStarted){
            speak(activityContext.getString(R.string.note_title_is) + returnedText +
                    activityContext.getString(R.string.press_again_note))
            noteTitle = returnedText
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