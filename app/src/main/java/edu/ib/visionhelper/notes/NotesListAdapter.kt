package edu.ib.visionhelper.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.ib.visionhelper.R

class NotesListAdapter(
    private val context: Context,
    private val arrayList: java.util.ArrayList<String>
) : BaseAdapter() {

    private lateinit var noteTitle: TextView

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val convertView = LayoutInflater.from(context).inflate(
            R.layout.activity_listview_notes_adapter,
            parent, false
        )
        noteTitle = convertView.findViewById(R.id.notetitle)
        noteTitle.text = arrayList[position]
        return convertView
    }
}