package info.mx.tracks.trackdetail.event

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.View
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import info.mx.tracks.R
import java.text.SimpleDateFormat
import java.util.*

class EventsViewBinder : SimpleCursorAdapter.ViewBinder {
    @SuppressLint("SimpleDateFormat")
    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        // Events2seriesRecord rec = Events2seriesRecord.fromCursor(cursor);
        when (view.id) {
            R.id.textDatum -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                (view as TextView).text = sdf.format(Date(cursor.getLong(columnIndex) * 1000))
                res = true
            }
            R.id.textSerie -> {
                view.visibility =
                    if (cursor.getString(columnIndex) == null || cursor.getString(columnIndex) == "") View.GONE else View.VISIBLE
                (view as TextView).text = cursor.getString(columnIndex)
                res = true
            }
            R.id.textKommentar -> {
                (view as TextView).text = cursor.getString(columnIndex)
                res = true
            }
        }
        return res
    }
}
