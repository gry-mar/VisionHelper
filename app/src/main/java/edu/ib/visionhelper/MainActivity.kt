package edu.ib.visionhelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.nl.translate.Translation.*
import edu.ib.visionhelper.calculator.CalculatorActivity
import edu.ib.visionhelper.call.CallActivity
import edu.ib.visionhelper.camera.CameraActivity
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.notes.NotesActivity
import edu.ib.visionhelper.zoomview.ZoomViewActivity
import java.util.*

class MainActivity : AppCompatActivity(){
    private lateinit var speechManager: SpeechManager
    private var preferences: PreferencesManager? = null
    private var granted = false
    private val REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)
        verifyPermission()

        val helperButton = findViewById<ImageButton>(R.id.mainhelperButton)
        helperButton.setOnClickListener{
                speechManager.speakOut(getString(R.string.main_helper_text), false)
        }

        val cameraButton = findViewById<ImageButton>(R.id.cameraButton)
        cameraButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)

        }

        val notesButton = findViewById<ImageButton>(R.id.notesButton)
        notesButton.setOnClickListener{
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)

        }

        val callButton = findViewById<ImageButton>(R.id.callButton)
        callButton.setOnClickListener{
            val intent = Intent(this, CallActivity::class.java)
            startActivity(intent)

        }
        val zoomButton = findViewById<ImageButton>(R.id.zoomTextButton)
        zoomButton.setOnClickListener{
            startActivity(Intent(this, ZoomViewActivity::class.java))
        }
        val calculatorButton = findViewById<ImageButton>(R.id.calculatorButton)
        calculatorButton.setOnClickListener{
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
    }
    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is paused
        speechManager.shutdownSpeaking()
        super.onDestroy()

    }

    override fun onPause() {
        speechManager.stopSpeaking()
        super.onPause()
    }


    /**
     * Checking permissions
     */
    private fun verifyPermission() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            granted = true
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, permissions, REQUEST_CODE)
        }
    }
}