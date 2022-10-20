package edu.ib.visionhelper.call

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R
import edu.ib.visionhelper.call.CallListAdapter
import edu.ib.visionhelper.call.CallListElement

class CallActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var arrayList: ArrayList<CallListElement> = ArrayList()
    var adapter: CallListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        listView = findViewById(R.id.listContacts)
        arrayList.add(CallListElement("Mama", 666666666));
        arrayList.add(CallListElement("Dziadek", 789563124));
        arrayList.add(CallListElement("Lekarz", 112444357));
        adapter = CallListAdapter(this, arrayList)
        listView.adapter = adapter
    }
}