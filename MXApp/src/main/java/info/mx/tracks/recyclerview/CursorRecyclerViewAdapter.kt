package info.mx.tracks.recyclerview

import android.database.Cursor
import android.database.DataSetObserver
import androidx.recyclerview.widget.RecyclerView

/**
 * [Gist](https://gist.github.com/skyfishjy/443b7448f59be978bc59)
 */
abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder?>(var cursor: Cursor?) : RecyclerView.Adapter<VH>() {

    private var dataValid: Boolean
    private var rowIdColumn: Int
    private val dataSetObserver: DataSetObserver?

    init {
        dataValid = cursor != null
        rowIdColumn = if (dataValid)
            cursor!!.getColumnIndex("_id")
        else
            -1

        dataSetObserver = NotifyingDataSetObserver()
        if (cursor != null) {
            cursor!!.registerDataSetObserver(dataSetObserver)
        }
    }

    override fun getItemCount(): Int {
        return if (dataValid && cursor != null) {
            cursor!!.count
        } else {
            0
        }
    }

    override fun getItemId(position: Int): Long {
        return if (dataValid && cursor != null && cursor!!.moveToPosition(position)) {
            cursor!!.getLong(rowIdColumn)
        } else
            0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor?)
    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        check(dataValid) { "this should only be called when the cursor is valid" }
        check(cursor!!.moveToPosition(position)) { "couldn't move cursor to position $position" }
        onBindViewHolder(viewHolder, cursor)
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be closed.
     */
    fun changeCursor(cursor: Cursor) {
        val old = swapCursor(cursor)
        old?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not* closed.
     */
    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor === cursor) {
            return null
        }
        val oldCursor = cursor
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver)
        }
        cursor = newCursor
        cursor?.let {
            if (dataSetObserver != null) {
                it.registerDataSetObserver(dataSetObserver)
            }
            rowIdColumn = it.getColumnIndexOrThrow("_id")
            dataValid = true
            notifyDataSetChanged()
        } ?: run {
            rowIdColumn = -1
            dataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            dataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            dataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

}
