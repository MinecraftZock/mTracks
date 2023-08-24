package info.mx.tracks.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.mx.tracks.R

/**
 * [Gist](https://gist.github.com/tuanchauict/c6c1eda617523de224c5)
 */
class TestHeaderCursorAdapter(val context: Context, cursor: Cursor?, sections: List<Section>?) :
    HeaderCursorRecyclerViewAdapter<RecyclerView.ViewHolder?>(cursor, sections) {

    internal inner class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)
        fun setHeader(header: String?) {
            textView.text = header
        }
    }

    internal inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text)

        @SuppressLint("SetTextI18n")
        fun setName(name: String) {
            textView.text = "Item - $name"
        }
    }

    override fun onBindSectionHeaderViewHolder(headerHolder: RecyclerView.ViewHolder?, header: String?) {
        (headerHolder as HeaderHolder).setHeader(header)
    }

    override fun onBindItemViewHolder(itemHolder: RecyclerView.ViewHolder?, cursor: Cursor?) {
        (itemHolder as ItemHolder).setName("Position = " + cursor?.position)
    }

    override fun onCreateSectionHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        //FIXME
        val view = LayoutInflater.from(context).inflate(R.layout.content_activity_filter, parent, false)
        return HeaderHolder(view)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        //FIXME
        val view = LayoutInflater.from(context).inflate(R.layout.content_activity_filter_country, parent, false)
        return ItemHolder(view)
    }
}
