package edu.ib.visionhelper.notes

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import edu.ib.visionhelper.R

class NotesActivity : AppCompatActivity() {

    lateinit var listView: ListView
    var arrayList: ArrayList<String> = ArrayList()
    var adapter: NotesListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        listView = findViewById(R.id.listNotes)
        arrayList.add("Przykładowa notatka 1")
        arrayList.add("Przykładowa notatka 2")
        arrayList.add("Przykładowa notatka 3")
        adapter = NotesListAdapter(this, arrayList)
        listView.adapter = adapter
    }
}