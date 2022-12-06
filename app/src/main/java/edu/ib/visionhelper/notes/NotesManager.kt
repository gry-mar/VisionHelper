package edu.ib.visionhelper.notes

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LifecycleOwner
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.SpeechRecognizerManager
import edu.ib.visionhelper.utils.VibrateUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * Manager class to handle NotesActivity logic
 */
@RequiresApi(Build.VERSION_CODES.S)
class NotesManager(context: Context, val activity: NotesActivity, val lifecycleOwner: LifecycleOwner) {

    private lateinit var removeButton: ImageButton
    private var notesFilesManager: NotesFilesManager = NotesFilesManager()
    var isRecordingStarted: Boolean = false
    var notesRecorderManager: NotesRecorderManager
    var speechManager: SpeechManager = SpeechManager(context)
    var speechManager2: SpeechManager = SpeechManager(context)
    var speechManager3: SpeechManager = SpeechManager(context)
    private var speechRecognizerManager: SpeechRecognizerManager = SpeechRecognizerManager(context)
    private var preferences: PreferencesManager? = null
    var arrayList: ArrayList<String> = ArrayList()
    var adapter: NotesListAdapter? = null
    var returnedText: String = ""
    private var speech: SpeechRecognizer
    private var recognizerIntent: Intent
    var addNoteStarted: Boolean = false
    var removeNoteStarted: Boolean = false
    var addNoteMessage: Boolean = false
    private var activityContext = context
    private lateinit var addButton: ImageButton
    private lateinit var playStopButton: ImageButton
    var noteTitle = ""

    init {
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


    /**
     * Function to play or stop sound from .mp3 existing file
     * @param playStopNotesButton - button that enables to stop/play sound if any item was selected
     * @param removeNoteButton - button clicked to start removing process
     * @param addButton - button to add note
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun handlePlayStopButton(playStopNotesButton: ImageButton, addButton: ImageButton, removeNoteButton: ImageButton) {
        this.playStopButton = playStopNotesButton
        if((addNoteStarted && !addNoteMessage) || removeNoteStarted){
            if (notesRecorderManager.mMediaPlayer?.isPlaying == true) {
                notesRecorderManager.stopSound()
                playStopButton.setImageResource(R.drawable.ic_play)
            }
        }else if (!addNoteMessage && !addNoteStarted) {
            if (notesRecorderManager.mMediaPlayer?.isPlaying == true) {
                notesRecorderManager.stopSound()
                playStopButton.setImageResource(R.drawable.ic_play)
            } else {
                if (adapter?.getItemSelected() != null) {
                    playStopButton.setImageResource(R.drawable.ic_stop)
                    notesRecorderManager.playSound(adapter?.getItemSelected()!!)
                }
            }
        }else if (addNoteMessage && !addNoteStarted) {
                playStopButton.setImageResource(R.drawable.ic_play)
                if (isRecordingStarted) {
                    notesRecorderManager.stopRecording()
                    isRecordingStarted = false
                    removeNoteButton.setImageResource(R.drawable.ic_remove_red)
                    removeNoteButton.clearColorFilter()
                    removeNoteButton.isClickable = true
                    removeNoteButton.isEnabled = true
                    addButton.clearColorFilter()
                    addButton.isClickable = true
                    addButton.isEnabled = true
                    VibrateUtil(activityContext).vibrate(300)
                    addNoteMessage = false
                    addNoteStarted = false
                    notesFilesManager.writeToFile(noteTitle, activityContext)
                    val file = notesFilesManager.readFile(activityContext)

                    var indexPrevious = 0
                    var index = 0
                    file.forEach { char: Char ->
                        if (char == ',') {
                            arrayList.add(file.substring(indexPrevious, index))
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

                    removeNoteButton.isClickable = false
                    removeNoteButton.isEnabled = false
                    removeNoteButton.setColorFilter(R.color.gray)

                    addButton.isClickable = false
                    addButton.isEnabled = false
                    addButton.setImageResource(R.drawable.ic_add_note)
                    addButton.setColorFilter(R.color.gray)

                    VibrateUtil(activityContext).vibrate(300)
                    playStopButton.setImageResource(R.drawable.ic_stop)
                    isRecordingStarted = true
                }
            }
    }

    /**
     * Handles results of input speech: result text given when new note title is added
     * or title is deleted
     *
     */
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
        returnedText = text.lowercase(Locale.getDefault())
        if (addNoteStarted){
            if(arrayList.contains(returnedText)){
                speak(activityContext.getString(R.string.note_already_exist))
            }else {
                speak(
                    activityContext.getString(R.string.note_title_is) + returnedText +
                            activityContext.getString(R.string.press_again_note)
                )
                noteTitle = returnedText
                while (speechManager.isFinishedSpeaking != 1) {
                    //wait for speech manager to finish speaking
                }
                addNoteMessage = true
                addNoteStarted = false
                return
            }
        }else if(removeNoteStarted){
            if(arrayList.contains(returnedText)){
                arrayList.remove(returnedText)
                notesFilesManager.deleteFile(returnedText, activityContext)
                speak(activityContext.getString(R.string.note_title_is) + returnedText)
                speechManager3.speakWithObservable(activityContext.getString(R.string.notes_remove_start))
                speechManager3.finished.observe(lifecycleOwner, {
                    if(it){
                        adapter = NotesListAdapter(activityContext, arrayList, this)
                        activity.finish()
                        activity.startActivity(activity.intent)
                    }
                })
            }else{
                speak(activityContext.getString(R.string.note_doesnt_exist))
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
     * Function to handle the beginning of adding new note
     */
    fun handleAddButton(addButton: ImageButton, removeNoteButton: ImageButton){
        this.addButton = addButton
        if (notesRecorderManager.mMediaPlayer?.isPlaying == true) {
            notesRecorderManager.stopSound()
            playStopButton.setImageResource(R.drawable.ic_play)
        }
        if (!isRecordingStarted) {
            if (!addNoteStarted) {
                removeNoteButton.isClickable = false
                removeNoteButton.isEnabled = false
                removeNoteButton.setImageResource(R.drawable.ic_remove_yellow)
                removeNoteButton.setColorFilter(R.color.gray)
                speak(activityContext.getString(R.string.notes_add_note_start))
                while (speechManager.isFinishedSpeaking != 1) {
                    //wait for speech manager to finish speaking
                }
                addNoteStarted = true

            } else {
                addNoteStarted = false
                removeNoteButton.isClickable = true
                removeNoteButton.isEnabled = true
                removeNoteButton.setImageResource(R.drawable.ic_remove_red)
                removeNoteButton.clearColorFilter()
                speak(activityContext.getString(R.string.note_exit_add))

            }
        }
    }

    /**
     * Function to handle the beginning of removing note
     */
    fun handleRemoveButton(removeNoteButton: ImageButton, addButton: ImageButton) {
        this.removeButton = removeNoteButton

        if (notesRecorderManager.mMediaPlayer?.isPlaying == true) {
            notesRecorderManager.stopSound()
            playStopButton.setImageResource(R.drawable.ic_play)
        }

        if (!isRecordingStarted) {
            if (!removeNoteStarted) {
                addButton.isClickable = false
                addButton.isEnabled = false
                addButton.setColorFilter(R.color.disabled_gray)
                activityContext.getString(R.string.notes_remove_start)

                removeNoteStarted = true

            } else {
                removeNoteStarted = false
                addButton.isClickable = true
                addButton.isEnabled = true
                addButton.clearColorFilter()

                speak(activityContext.getString(R.string.note_exit_remove))

            }
        }
    }
}