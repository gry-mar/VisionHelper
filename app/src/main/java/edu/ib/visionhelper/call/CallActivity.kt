package edu.ib.visionhelper.call
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R
import android.widget.AbsListView


class CallActivity : AppCompatActivity(), RecognitionListener {

    private lateinit var listView: ListView
    private lateinit var viewManager: CallManager
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        viewManager = CallManager(this, this)

        val callButton = findViewById<ImageButton>(R.id.callContactButton)
        callButton.setOnLongClickListener {
            viewManager.listen()
            true
        }

        val addContactButton = findViewById<ImageButton>(R.id.addContactButton)
        addContactButton.setOnClickListener{
            viewManager.handleContactAdd(callButton, addContactButton)

        }


        val helperButton = findViewById<ImageButton>(R.id.helperCallButton)
        helperButton.setOnClickListener {
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    viewManager.stopSpeaking()
                    false
                } else {
                    viewManager.speak(getString(R.string.call_helper_text))
                    true
                }
            }else{
                viewManager.stopSpeaking()
                isSpeaking = false
            }
            isFirstSpeech = false
        }

        listView = findViewById(R.id.listContacts)
        listView.adapter = viewManager.adapter
        listView.setOnScrollListener(object :AbsListView.OnScrollListener {
            override fun onScroll(p0: AbsListView?, FirstVisibleItem: Int, i2: Int, i3: Int) {
                viewManager.stopSpeaking()
            }
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
                viewManager.stopSpeaking()
            }
        })
    }


    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        viewManager.stopSpeaking()
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
        viewManager.returnedText = errorMessage
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
        viewManager.handleResults(results)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i("logTag", "partial results")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i("logTag", "on event")
    }

}