package info.hannes.mxadmin.download

import android.database.Cursor
import android.view.View
import info.mx.tracks.R
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import java.text.SimpleDateFormat
import java.util.*

class ViewBinderDownloadSite : SimpleCursorAdapter.ViewBinder {
    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        val i = view.id
        if (i == R.id.txCreated) {
            val value = cursor.getLong(columnIndex)
            val datumVon = SimpleDateFormat(
                "yyy.MM.dd  HH:mm:ss",
                Locale.getDefault()
            ).format(Date(value * 1000))
            (view as TextView).text = datumVon
            res = true
        }
        return res
    }
}