package edu.ib.visionhelper.zoomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

class ZoomViewActivity : AppCompatActivity() {

    private lateinit var speechManager: SpeechManager
    private var preferences: PreferencesManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_view)

        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)

        val helperButton = findViewById<ImageButton>(R.id.btnHelperZoomView)
        helperButton.setOnClickListener{
            speechManager.speakOut(getString(R.string.zoom_helper_text), true)
        }
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }
}