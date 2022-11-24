package edu.ib.visionhelper.notes

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.AbsListView
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

class NotesActivity : AppCompatActivity(), RecognitionListener {

    lateinit var listView: ListView
    var arrayList: ArrayList<String> = ArrayList()
    var adapter: NotesListAdapter? = null
    private lateinit var speechManager: NotesManager
    private var preferences: PreferencesManager? = null
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        speechManager = NotesManager(this, this)
        preferences = PreferencesManager(applicationContext)

        val helperButton = findViewById<ImageButton>(R.id.helperNotesButton)
        helperButton.setOnClickListener{
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    speechManager.stopSpeaking()
                    false
                } else {
                    speechManager.speak(getString(R.string.notes_helper_text))
                    true
                }
            }else{
                speechManager.stopSpeaking()
                isSpeaking = false
            }
            isFirstSpeech = false
        }

//        val addNoteBtn = findViewById<ImageButton>(R.id.notesButton)
//        addNoteBtn.setOnClickListener {
//            Log.i("NotesActivity", "add note btn clicked")
//        }

        listView = findViewById(R.id.listNotes)

        listView.adapter = adapter

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(p0: AbsListView?, FirstVisibleItem: Int, i2: Int, i3: Int) {
                speechManager.stopSpeaking()
            }
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                speechManager.stopSpeaking()
            }
        })
    }
    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.i("logTag", "start")
    }

    override fun onBeginningOfSpeech() {
        Log.i("logTag", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i("logTag", "during")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.i("logTag", "buffer received")
    }

    override fun onEndOfSpeech() {
        Log.i("logTag", "end")
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d("tag", "FAILED $errorMessage")
        speechManager.returnedText = errorMessage
    }

    private fun getErrorText(error: Int): String {
        val message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }


    override fun onResults(results: Bundle?) {
        speechManager.handleResults(results)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i("logTag", "partial results")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i("logTag", "on event")
    }

}