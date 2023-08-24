package info.mx.tracks.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import info.mx.tracks.R

class ArrayAdapterRightAlign(ctx: Context, resource: Int, private val objects: Array<CharSequence>) :
    ArrayAdapter<CharSequence>(ctx, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("ViewHolder")
        val view = inflater.inflate(R.layout.spinner_item_right, parent, false)
        val textView = view.findViewById<TextView>(R.id.textSpinner)
        textView.text = objects[position]
        return view
    }

    companion object {
        fun createFromResource(
            context: Context,
            textArrayResId: Int,
            textViewResId: Int
        ): ArrayAdapterRightAlign {
            val strings = context.resources.getTextArray(textArrayResId)
            return ArrayAdapterRightAlign(context, textViewResId, strings)
        }
    }
}