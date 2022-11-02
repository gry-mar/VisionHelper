package edu.ib.visionhelper.zoomview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.widget.ImageButton
import android.widget.TextView
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.ZoomViewManager

class ZoomViewActivity : AppCompatActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var zoomViewManager: ZoomViewManager
    private var preferences: PreferencesManager? = null
    private lateinit var zoomedText: TextView
    private lateinit var btnZoomIn : ImageButton
    private lateinit var btnZoomOut : ImageButton
    private lateinit var btnConfirmTextSize : ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_view)

        zoomedText = findViewById(R.id.tvTextZoomView)
        btnZoomIn = findViewById(R.id.btnZoomTextIn)
        btnZoomOut = findViewById(R.id.btnZoomTextOut)
        btnConfirmTextSize = findViewById(R.id.btnConfirmTextSizeZoomView)

        speechManager = SpeechManager(this)
        zoomViewManager = ZoomViewManager(this)
        preferences = PreferencesManager(applicationContext)
        zoomedText.textSize = zoomViewManager.textSize

        val helperButton = findViewById<ImageButton>(R.id.btnHelperZoomView)
        helperButton.setOnClickListener{
            speechManager.speakOut(getString(R.string.zoom_helper_text))
        }

        btnZoomIn.setOnClickListener{
            if(zoomViewManager.textSize >= 46.0F) {
                speechManager.speakOut(getString(R.string.zoomed_text_too_big))
            } else {
                zoomViewManager.zoomIn()
                zoomedText.textSize = zoomViewManager.textSize
            }
        }

        btnZoomOut.setOnClickListener{
            if(zoomViewManager.textSize <= 14.0F) {
                speechManager.speakOut(getString(R.string.zoomed_text_too_small))
            } else {
                zoomViewManager.zoomOut()
                zoomedText.textSize = zoomViewManager.textSize
            }
        }

        btnConfirmTextSize.setOnClickListener {
            zoomViewManager.confirmTextSize()
            speechManager.speakOut(getString(R.string.confirm_zoomed_text) +
                    zoomViewManager.textSize.toInt().toString())
        }

    }


    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }
}