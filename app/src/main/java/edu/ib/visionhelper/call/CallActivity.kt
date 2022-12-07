package edu.ib.visionhelper.call
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.MainActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.utils.VibrateUtil
import kotlinx.android.synthetic.main.activity_call.*

/**
 * Activity that is responsible for making phone calls and
 * contact list management (from UI side)
 */
class CallActivity : AppCompatActivity(), RecognitionListener {

    private lateinit var listView: ListView
    private lateinit var viewManager: CallManager
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true
    private lateinit var keyboardLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        viewManager = CallManager(this, this, this)

        keyboardLayout = findViewById(R.id.numericKeyboard)
        val addContactButton = findViewById<ImageButton>(R.id.addContactButton)
        val callButton = findViewById<ImageButton>(R.id.callContactButton)
        callButton.setOnLongClickListener {
            viewManager.isActionClosed = false
                    viewManager.listen()
            true
        }

        val deleteContactButton = findViewById<ImageButton>(R.id.deleteContactButton)
        deleteContactButton.setOnClickListener{
            viewManager.handleRemoveContact(callButton, deleteContactButton, addContactButton)
        }
        addContactButton.setOnClickListener{
            viewManager.handleContactAdd(callButton, addContactButton, deleteContactButton)
        }
        viewManager.updateUI.observe(this, {
            if(it){
                callButton.setBackgroundResource(R.drawable.shape_circle_green)
                addContactButton.setImageResource(R.drawable.ic_cancel)
                    deleteContactButton.setColorFilter(R.color.black)
                    deleteContactButton.isEnabled = false
                deleteContactButton.isClickable = false
            }
            else {
                callButton.setBackgroundResource(R.drawable.shape_circle_blue)
                addContactButton.setImageResource(R.drawable.ic_add_note)
                deleteContactButton.clearColorFilter()
                deleteContactButton.isEnabled = true
                deleteContactButton.isClickable = true
            }
        })

        viewManager.updateUIDelete.observe(this, {
            if(it){
                deleteContactButton.setImageResource(R.drawable.ic_cancel_red)
                callButton.setBackgroundResource(R.drawable.shape_circle_red)
                addContactButton.setColorFilter(R.color.black)
            } else {
                callButton.setBackgroundResource(R.drawable.shape_circle_blue)
                deleteContactButton.setImageResource(R.drawable.ic_remove_red)
                addContactButton.clearColorFilter()
            }
        })

        callButton.setOnClickListener{
            VibrateUtil(this).vibrate(250)
            handleContactNumberAdd()
            }
        viewManager.longPressActivated.observe(this, {
            callButton.isLongClickable = it
            callButton.isClickable = !it
        })
        viewManager.speechManagerThird.finished.observe( this, {
            if(it){
                finish()
                startActivity(intent)
            }
        })
        viewManager.speechManager7.finished.observe( this, {
            if(it){
                finish()
                startActivity(intent)
            }
        })

        val helperButton = findViewById<ImageButton>(R.id.helperCallButton)
        helperButton.setOnClickListener {
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    viewManager.stopSpeaking()
                    false
                } else {
                    viewManager.speak(getString(R.string.call_helper_text), 1)
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

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromCall)
        btnBack.setOnClickListener{
            finish()
        }
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

    /**
     * Method that returns readable error description
     * @param error - Integer error code
     */
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
        if(!viewManager.isActionClosed) {
            viewManager.handleResults(results, addContactButton)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i("logTag", "partial results")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i("logTag", "on event")
    }

    /**
     * Function that handles adding contact number and redirects to
     * appropriate methods in View Manager
     */
    private fun handleContactNumberAdd() {
        if (viewManager.addContactNumber.value == true) {
            keyboardLayout.visibility = View.VISIBLE
            var contactNumber = ""
            val button1 = findViewById<Button>(R.id.button1)
            button1.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "1"
            }
            val button2 = findViewById<Button>(R.id.button2)
            button2.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "2"
            }
            val button3 = findViewById<Button>(R.id.button3)
            button3.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "3"
            }
            val button4 = findViewById<Button>(R.id.button4)
            button4.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "4"
            }
            val button5 = findViewById<Button>(R.id.button5)
            button5.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "5"
            }
            val button6 = findViewById<Button>(R.id.button6)
            button6.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "6"
            }
            val button7 = findViewById<Button>(R.id.button7)
            button7.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "7"
            }
            val button8 = findViewById<Button>(R.id.button8)
            button8.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "8"
            }
            val button9 = findViewById<Button>(R.id.button9)
            button9.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "9"
            }
            val button0 = findViewById<Button>(R.id.button0)
            button0.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                contactNumber += "0"
            }
            println("Contact number$contactNumber")
            val buttonConfirm = findViewById<ImageButton>(R.id.buttonConfirmContactNumber)
            buttonConfirm.setOnClickListener {
                VibrateUtil(this).vibrate(150)
                viewManager.handleCheckNumber(contactNumber, numericKeyboard)
                contactNumber = ""
            }
        }
    }

}