package edu.ib.visionhelper.notes

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

class NotesActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var arrayList: ArrayList<String> = ArrayList()
    var adapter: NotesListAdapter? = null
    private lateinit var speechManager: SpeechManager
    private var preferences: PreferencesManager? = null
    private var isSpeaking: Boolean = false
    private var isFirstSpeech: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)

        val helperButton = findViewById<ImageButton>(R.id.helperNotesButton)
        helperButton.setOnClickListener{
            if(!isFirstSpeech) {
                isSpeaking = if (isSpeaking) {
                    speechManager.stopSpeaking()
                    false
                } else {
                    speechManager.speakOut(getString(R.string.notes_helper_text))
                    true
                }
            }else{
                speechManager.stopSpeaking()
                isSpeaking = false
            }
            isFirstSpeech = false
        }

        listView = findViewById(R.id.listNotes)
        arrayList.add("Przykładowa notatka 1")
        arrayList.add("Przykładowa notatka 2")
        arrayList.add("Przykładowa notatka 3")
        adapter = NotesListAdapter(this, arrayList)
        listView.adapter = adapter
    }
    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }
}