package edu.ib.visionhelper.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import kotlinx.android.synthetic.main.activity_camera.*

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

        if (isAllPermissionsGranted) startCamera() else requestPermissions()

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

    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = CameraActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionsGranted) {
                startCamera()
            } else {
                Snackbar.make(preview_view, "Camera permission not granted. \nCannot perform magic ritual.", Snackbar.LENGTH_LONG).setAction("Retry") {
                    requestPermissions()
                }.show()
            }
        }
    }

    private val cameraAdapter = CameraAdapter {
        textFound = it
        Log.d(TAG, "Text Found: $it")
    }

    private fun startCamera() {
        cameraAdapter.startCamera(this, this, preview_view.surfaceProvider)
        Log.d(TAG, "Camera started")
    }


}