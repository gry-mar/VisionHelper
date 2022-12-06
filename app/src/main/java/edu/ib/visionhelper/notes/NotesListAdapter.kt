package edu.ib.visionhelper.notes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.TextSizePreferencesManager


@RequiresApi(Build.VERSION_CODES.S)
class NotesListAdapter(
    private val context: Context,
    private val arrayList: java.util.ArrayList<String>,
) : BaseAdapter() {

    private lateinit var noteTitle: TextView
    private var textPreferences: TextSizePreferencesManager? = null
    private var textSize: Float
    private var itemSelected: String? = null
    private var lastSelected: String = ""
    private var isSelected: Boolean = false

    init {
        textPreferences = TextSizePreferencesManager(context)
        textSize = textPreferences!!.textSize
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    fun getItemSelected(): String? {
        return itemSelected
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
        val holder = ViewHolderContact(convertView)
        val title = holder.title.text

        if (title == lastSelected) {
            if (!isSelected) {
                convertView.setBackgroundColor(Color.BLUE)
                itemSelected = arrayList[position].replace(' ', '_')
                isSelected = true

            } else {
                itemSelected = null
                convertView.setBackgroundColor(Color.BLACK)
                isSelected = false
            }
        } else {
            convertView.setBackgroundColor(Color.BLACK)
        }

        convertView.setOnClickListener {
            val item: Int = getItem(position) as Int
            val selectedItem = arrayList[item]

            lastSelected = selectedItem
            notifyDataSetChanged()
        }
        return convertView
    }

    class ViewHolderContact(itemView: View) {
        val title: TextView = itemView.findViewById(R.id.notetitle)
    }


}