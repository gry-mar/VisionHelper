package edu.ib.visionhelper.call

import android.annotation.SuppressLint
import android.content.Context
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.ib.visionhelper.R
import edu.ib.visionhelper.manager.SpeechManager
import edu.ib.visionhelper.manager.TextSizePreferencesManager

class CallListAdapter(
   private val context: Context,
    private val arrayList: ArrayList<CallListElement>,
   private val viewManager: CallManager
) : BaseAdapter() {

    private lateinit var contactName: TextView
    private lateinit var contactNumber: TextView
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
            R.layout.activity_listview_call_adapter,
            parent, false
        )
        contactName = convertView.findViewById(R.id.contactName)
        contactName.textSize = textSize
        contactNumber = convertView.findViewById(R.id.contactNumber)
        contactNumber.textSize = textSize
        contactName.text = arrayList[position].contactName
        contactNumber.text = arrayList[position].contactNumber.toString()

        convertView.setOnClickListener {
            val stringArrayWithChars: ArrayList<String> = ArrayList()
            val item: Int = getItem(position) as Int
            val charArray = arrayList[item].contactNumber.toString().toCharArray()
            charArray.forEach { char ->
                stringArrayWithChars.add(char.toString())
            }
            viewManager.speak(arrayList[item].contactName + ", telefon: " +
            stringArrayWithChars.toString())
        }
        return convertView
    }


}