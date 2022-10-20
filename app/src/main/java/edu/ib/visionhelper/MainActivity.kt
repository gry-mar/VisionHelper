package edu.ib.visionhelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import edu.ib.visionhelper.call.CallActivity
import edu.ib.visionhelper.camera.CameraActivity
import edu.ib.visionhelper.notes.NotesActivity

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
    }
}