package edu.ib.visionhelper.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Activity responsible for reading text from Camera View
 */
class CameraActivity : AppCompatActivity() {
    private lateinit var speechManager: SpeechManager
    private var preferences: PreferencesManager? = null
    private var textFound = ""
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)

        TextToSpeech.OnInitListener {
            if (preferences!!.cameraFirstTimeLaunched == 0) {
                speechManager.speakOut(getString(R.string.camera_helper_text))
                preferences!!.cameraFirstTimeLaunched = 1
            }
        }

        val helperButton = findViewById<ImageButton>(R.id.helperCameraButton)
        helperButton.setOnClickListener{
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    speechManager.stopSpeaking()
                    false
                } else {
                    speechManager.speakOut(getString(R.string.camera_helper_text))
                    true
                }
            }else{
                speechManager.stopSpeaking()
                isSpeaking = false
            }
            isFirstSpeech = false
        }

        startCamera()

        val startSpeakingButton = findViewById<ImageButton>(R.id.playButton)
        startSpeakingButton.setOnClickListener{
            speechManager.speakOut(textFound)
        }

        val stopSpeakingButton = findViewById<ImageButton>(R.id.stopButton)
        stopSpeakingButton.setOnClickListener{
            speechManager.stopSpeaking()
        }
    }

    public override fun onDestroy() {
        cameraAdapter.shutdown()
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()

        super.onDestroy()
    }

    /**
     * Sets TAG for this activity
     */
    companion object {
        private val TAG = CameraActivity::class.java.name
    }

    /**
     * Camera adapter instance with handling OnTextFound
     * as assigning it to Camera Activity property textFound
     */
    private val cameraAdapter = CameraAdapter {
        textFound = it
        Log.d(TAG, "Text Found: $it")
    }

    /**
     * Method that starts camera using CameraAdapter class
     */
    private fun startCamera() {
        cameraAdapter.startCamera(this, this, preview_view.surfaceProvider)
        Log.d(TAG, "Camera started")
    }


}