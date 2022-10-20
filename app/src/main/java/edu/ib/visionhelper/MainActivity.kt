package edu.ib.visionhelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import edu.ib.visionhelper.calculator.CalculatorActivity
import edu.ib.visionhelper.call.CallActivity
import edu.ib.visionhelper.camera.CameraActivity
import edu.ib.visionhelper.notes.NotesActivity
import edu.ib.visionhelper.zoomview.ZoomViewActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}