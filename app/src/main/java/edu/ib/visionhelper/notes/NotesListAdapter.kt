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
import edu.ib.visionhelper.call.CallManager
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.TextSizePreferencesManager

@RequiresApi(Build.VERSION_CODES.S)
class NotesListAdapter(
    private val context: Context,
    private val arrayList: java.util.ArrayList<String>,
    private val viewManager: NotesManager,
) : BaseAdapter() {

    private lateinit var noteTitle: TextView
    private var textPreferences: TextSizePreferencesManager? = null
    private var textSize: Float
    private var itemSelected: String? = null

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
        var save = -1
        noteTitle = convertView.findViewById(R.id.notetitle)
        noteTitle.textSize = textSize
        noteTitle.text = arrayList[position]

        convertView.setOnClickListener {
            val item: Int = getItem(position) as Int
            viewManager.speak(arrayList[item])
            parent.getChildAt(position).setBackgroundColor(Color.BLUE);

            if (save != -1){
                parent.getChildAt(position).setBackgroundColor(Color.BLACK);
            }else if(save == position){
                parent.getChildAt(position).setBackgroundColor(Color.BLACK);
            }
            save = position;

            if(itemSelected == null) {
                itemSelected = arrayList[item].replace(' ', '_')
            }else {
                itemSelected = null
            }
        }

        return convertView
    }
}