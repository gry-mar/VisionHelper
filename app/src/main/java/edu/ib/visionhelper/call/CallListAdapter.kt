package edu.ib.visionhelper.call

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.ib.visionhelper.R

class CallListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<CallListElement>,
) : BaseAdapter() {

    private lateinit var contactName: TextView
    private lateinit var contactNumber: TextView

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
            R.layout.activity_listview_call_adapter,
            parent, false
        )
        contactName = convertView.findViewById(R.id.contactName)
        contactNumber = convertView.findViewById(R.id.contactNumber)
        contactName.text = arrayList[position].contactName
        contactNumber.text = arrayList[position].contactNumber.toString()

        return convertView
    }
}