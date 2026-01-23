package info.mx.tracks.recyclerview

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import info.mx.tracks.R

/**
 * Created by skyfishjy on 10/31/14.
 * [Gist](https://gist.github.com/skyfishjy/443b7448f59be978bc59)
 */
class TestCursorAdapter(cursor: Cursor) : CursorRecyclerViewAdapter<TestCursorAdapter.ViewHolder>(cursor) {

    class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imagePicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //FIXME
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_imageslider, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor?) {
        val record = MyListItem.fromCursor(cursor)!!
        viewHolder.imageView.contentDescription = record.name
    }
}
