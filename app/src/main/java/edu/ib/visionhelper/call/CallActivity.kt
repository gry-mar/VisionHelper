package edu.ib.visionhelper.call

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.PreferencesManager
import edu.ib.visionhelper.manager.SpeechManager

class CallActivity : AppCompatActivity() {
    private lateinit var speechManager: SpeechManager
    private var preferences: PreferencesManager? = null
    lateinit var listView: ListView
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        speechManager = SpeechManager(this)
        preferences = PreferencesManager(applicationContext)

        val helperButton = findViewById<ImageButton>(R.id.helperCallButton)
        helperButton.setOnClickListener{
            speechManager.speakOut(getString(R.string.call_helper_text), true)
        }

        listView = findViewById(R.id.listContacts)
        arrayList.add(CallListElement("Mama", 666666666));
        arrayList.add(CallListElement("Dziadek", 789563124));
        arrayList.add(CallListElement("Lekarz", 112444357));
        adapter = CallListAdapter(this, arrayList)
        listView.adapter = adapter
    }

    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        speechManager.stopSpeaking()
        super.onDestroy()
    }
}