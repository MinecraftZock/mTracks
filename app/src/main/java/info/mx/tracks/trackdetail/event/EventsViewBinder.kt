package info.mx.tracks.trackdetail.event

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.R
import info.mx.core_generated.sqlite.MxInfoDBContract
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date

class EventsViewBinder : SimpleCursorAdapter.ViewBinder {

    var tracksRestID = 0L

    @SuppressLint("SimpleDateFormat")
    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        // Events2seriesRecord rec = Events2seriesRecord.fromCursor(cursor);
        when (view.id) {
            R.id.textDatum -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                (view as TextView).text = sdf.format(Date(cursor.getLong(columnIndex) * 1000))
                view.setOnClickListener { Timber.d("click textDatum") }
                view.setOnLongClickListener(object : AdapterView.OnItemLongClickListener, View.OnLongClickListener {
                    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
                        Timber.w("() id=$id tracksRestID=$tracksRestID")
                        return true
                    }

                    override fun onLongClick(v: View?): Boolean {
                        val id = cursor.getLong(0)
                        Timber.d("() id=$id tracksRestID=$tracksRestID")
                        // FIXME pointless, it needs approve = -1 and only admin should be allowed to do this
                        SQuery.newQuery()
                            .expr(MxInfoDBContract.Events.TRACK_REST_ID, SQuery.Op.EQ, tracksRestID)
                            .expr(MxInfoDBContract.Events._ID, SQuery.Op.EQ, id)
                            .delete(MxInfoDBContract.Events.CONTENT_URI)
                        return true
                    }
                })
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
