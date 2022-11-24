package edu.ib.visionhelper.notes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.ib.visionhelper.R
import edu.ib.visionhelper.call.CallManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.TextSizePreferencesManager

class NotesListAdapter(
    private val context: Context,
    private val arrayList: java.util.ArrayList<String>,
    private val viewManager: NotesManager
) : BaseAdapter() {

    private lateinit var noteTitle: TextView
    private var textPreferences: TextSizePreferencesManager? = null
    private var textSize: Float

    init {
        textPreferences = TextSizePreferencesManager(context)
        textSize = textPreferences!!.textSize
    }
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val convertView = LayoutInflater.from(context).inflate(
            R.layout.activity_listview_notes_adapter,
            parent, false
        )
        noteTitle = convertView.findViewById(R.id.notetitle)
        noteTitle.textSize = textSize
        noteTitle.text = arrayList[position]

        convertView.setOnClickListener {
            val item: Int = getItem(position) as Int
            viewManager.speak(arrayList[item])
        }

        return convertView
    }
}