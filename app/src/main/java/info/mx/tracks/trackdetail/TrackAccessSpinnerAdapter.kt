package info.mx.tracks.trackdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import info.mx.tracks.R
import java.util.*

class TrackAccessSpinnerAdapter(var context: Context) : BaseAdapter(), SpinnerAdapter {
    private var spinnerElements: ArrayList<TrackAccessElement>? = null

    init {
        val spinnerEntries = context.resources.getStringArray(R.array.access_list)
        val spinnerKeys = context.resources.getStringArray(R.array.access_keylist)
        spinnerElements = ArrayList<TrackAccessElement>().apply {
            for (i in spinnerEntries.indices) {
                val element = TrackAccessElement(spinnerKeys[i], spinnerEntries[i])
                add(element)
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        }

        val element = spinnerElements!![position]
        val title = view!!.findViewById<TextView>(android.R.id.text1)
        title.text = element.text
        title.setCompoundDrawablesWithIntrinsicBounds(element.imageResId, 0, 0, 0)
        return view
    }

    override fun getCount(): Int {
        return spinnerElements!!.size
    }

    override fun getItem(position: Int): TrackAccessElement {
        return spinnerElements!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getPositionById(key: String): Int {
        var res = 0
        for (elem in spinnerElements!!) {
            if (elem.id == key) {
                break
            }
            res++
        }
        return if (res >= spinnerElements!!.size) 0 else res
    }

}
