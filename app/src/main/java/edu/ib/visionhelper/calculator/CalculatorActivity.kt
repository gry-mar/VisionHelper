package edu.ib.visionhelper.calculator

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.CalculatorManager

import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager


class CalculatorActivity : AppCompatActivity(), RecognitionListener {
    private val permission = 100
    private lateinit var returnedText: TextView
    private lateinit var finalText: TextView
    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var btnMicrophone : ImageButton
    private var logTag = "VoiceRecognitionActivity"
    private lateinit var speechManager: SpeechManager
    private lateinit var calculatorManager: CalculatorManager
    private var preferences: PreferencesManager? = null
    private lateinit var textArray : MutableList<String>
    private var finalNumber : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        btnMicrophone = findViewById(R.id.btnSoundCalculator)
        returnedText = findViewById(R.id.tvEquationCalculator)
        finalText = findViewById(R.id.tvResultCalculator)

        calculatorManager = CalculatorManager();

        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(logTag, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

        btnMicrophone.setOnLongClickListener {
            returnedText.clearComposingText()
            Toast.makeText(applicationContext, "Button Long Pressed", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this@CalculatorActivity,
                arrayOf(android.Manifest.permission.RECORD_AUDIO), permission)
            true
        }

        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)

        val helperButton = findViewById<ImageButton>(R.id.helperCalculatorButton)
        helperButton.setOnClickListener{
            speechManager.speakOut(getString(R.string.calculator_helper_text))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent)
            } else {
                Toast.makeText(this@CalculatorActivity, "Permission Denied!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        speech.destroy()
        Log.i(logTag, "destroy")
    }
    override fun onReadyForSpeech(params: Bundle?) {
        Log.i(logTag, "Start")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onPartialResults(partialResults: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onBeginningOfSpeech() {
        Log.i(logTag, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(p0: Float) {
        Log.i(logTag, "DuringRecognition")
    }

    override fun onEndOfSpeech() {
        Log.i(logTag, "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d(logTag, "FAILED $errorMessage")
        returnedText.text = errorMessage
    }

    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
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
        Log.i(logTag, "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        returnedText.text = text
        println(calculatorManager.textSeparator(text))
        textArray = calculatorManager.textSeparator(text)
        finalNumber = calculatorManager.textAnalizer(textArray)
        finalText.text = finalNumber.toString()
    }


    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }

}