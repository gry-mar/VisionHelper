package edu.ib.visionhelper.zoomview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.TextView
import edu.ib.visionhelper.MainActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.calculator.CalculatorActivity
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

class ZoomViewActivity : AppCompatActivity() {

    private lateinit var speechManager: SpeechManager
    private lateinit var zoomViewManager: ZoomViewManager
    private var preferences: PreferencesManager? = null
    private lateinit var zoomedText: TextView
    private lateinit var btnZoomIn : ImageButton
    private lateinit var btnZoomOut : ImageButton
    private lateinit var btnConfirmTextSize : ImageButton
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true


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

        TextToSpeech.OnInitListener {
            if (preferences!!.zoomFirstTimeLaunched == 0) {
                speechManager.speakOut(getString(R.string.zoom_helper_text))
                preferences!!.zoomFirstTimeLaunched = 1
            }
        }


        val helperButton = findViewById<ImageButton>(R.id.btnHelperZoomView)
        helperButton.setOnClickListener{
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    speechManager.stopSpeaking()
                    false
                } else {
                    speechManager.speakOut(getString(R.string.zoom_helper_text))
                    true
                }
            }else{
                speechManager.stopSpeaking()
                isSpeaking = false
            }
            isFirstSpeech = false
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

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromZoom)
        btnBack.setOnClickListener{
            finish()
        }

    }


    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }
}