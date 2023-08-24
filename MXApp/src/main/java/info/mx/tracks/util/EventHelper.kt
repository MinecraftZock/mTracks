package info.mx.tracks.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxApplication.Companion.isGoogleTests
import info.mx.tracks.MxCoreApplication.Companion.doSync
import info.mx.tracks.R
import info.mx.tracks.sqlite.EventsRecord
import info.mx.tracks.sqlite.TracksRecord
import timber.log.Timber
import java.util.*

class EventHelper {
    private var datePickerEvent: DatePicker? = null
    private var edtComment: EditText? = null
    fun doAddEvent(context: Context, trackRec: TracksRecord?) {
        // on add eventId is not used
        doEditNote(context, 0, trackRec)
    }

    private fun doEditNote(context: Context, eventId: Int, trackRec: TracksRecord?) {
        if (trackRec == null || trackRec.trackname == null) {
            return
        }
        Timber.d(trackRec.trackname)
        record = EventsRecord.get(eventId.toLong())
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(if (eventId == 0) R.string.event_add else R.string.event_edit))
        builder.setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int -> }
        builder.setPositiveButton(R.string.done) { _: DialogInterface?, _: Int ->
            if (isGoogleTests) {
                Toast.makeText(context, "you test the app, event is ignored", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (record == null) {
                    record = EventsRecord()
                }
                val cal = Calendar.getInstance()
                cal[datePickerEvent!!.year, datePickerEvent!!.month] = datePickerEvent!!.dayOfMonth
                val value = cal.time
                record!!.eventDate = value.time / 1000
                record!!.comment = edtComment!!.text.toString()
                record!!.trackRestId = trackRec.restId
                record!!.approved = 0
                record!!.save()
                doSync(true, true, BuildConfig.FLAVOR)
            }
        }
        val dlg = builder.create()
        val inflater = dlg.layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.dialog_event_edit, null)
        datePickerEvent = view.findViewById(R.id.datePickerEvent)
        edtComment = view.findViewById(R.id.edtEventComment)
        if (record != null) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = record!!.eventDate
            datePickerEvent?.updateDate(
                cal[Calendar.YEAR],
                cal[Calendar.MONTH],
                cal[Calendar.DAY_OF_MONTH]
            )
            edtComment?.setText(record!!.comment)
        }
        dlg.setView(view)
        dlg.show()
    }

    companion object {
        private var record: EventsRecord? = null
    }
}