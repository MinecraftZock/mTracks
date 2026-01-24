package info.mx.tracks.recyclerview

import android.database.Cursor

class MyListItem {
    var name: String? = null

    companion object {
        fun fromCursor(cursor: Cursor?): MyListItem? {
            //TODO return your MyListItem from cursor.
            return null
        }
    }
}
