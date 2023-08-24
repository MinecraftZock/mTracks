package info.hannes.mxadmin.download

import android.database.Cursor
import android.view.View
import info.mx.tracks.R
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import info.hannes.mechadmin_gen.sqlite.QuellFileRecord
import java.text.SimpleDateFormat
import java.util.*

class ViewBinderQuellFile : SimpleCursorAdapter.ViewBinder {
    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        val i = view.id
        if (i == R.id.txCreatedate) {
            val `val` = cursor.getLong(columnIndex)
            val datumVon = SimpleDateFormat(
                "yyy.MM.dd  HH:mm:ss",
                Locale.getDefault()
            ).format(Date(`val` * 1000))
            (view as TextView).text = datumVon
            res = true
        } else if (i == R.id.webView) {
            if (cursor.getString(columnIndex).contains("<!DOCTYPE html>")) {
                (view as TextView).text = cursor.getString(columnIndex)
            } else {
                val rec = QuellFileRecord.fromCursor(cursor)
                (view as TextView).text = rec.log
            }
            view.setVisibility(
                if (!cursor.getString(columnIndex)
                        .contains("<!DOCTYPE html>")
                ) View.GONE else View.VISIBLE
            )
            res = true

            //		case R.id.btnExpand:
            //			view.setVisibility(cursor.getString(columnIndex).contains("Exception") ? View.GONE : View.VISIBLE);
            //			view.setOnClickListener(new OnClickListener() {
            //
            //				@Override
            //				public void onClick(View v) {
            //					// TODO Auto-generated method stub
            //				}
            //			});
            //			res = true;
            //			break;
        }
        return res
    }
}