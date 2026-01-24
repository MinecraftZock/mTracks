package info.mx.tracks.trackdetail.comment

import android.content.Context
import android.database.Cursor
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import info.mx.tracks.R
import info.mx.tracks.util.getDrawableIdentifier
import java.util.Date
import java.util.Locale

class CommentViewBinder(private val context: Context) : androidx.cursoradapter.widget.SimpleCursorAdapter.ViewBinder {

    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        val valueS = cursor.getString(columnIndex)
        when (view.id) {
            R.id.comlst_username -> {
                if (valueS == null || valueS == "") {
                    (view as TextView).text = context.getString(R.string.noname)
                } else {
                    (view as TextView).text = valueS
                }
                res = true
            }
            R.id.comlst_country -> {
                val valueL = cursor.getString(columnIndex).lowercase(Locale.getDefault())
                val id = context.resources.getDrawableIdentifier(valueL, context.packageName)
                (view as ImageView).setImageResource(id)
                res = true
            }
            R.id.comlst_note -> {
                if (valueS == "") {
                    view.visibility = View.GONE
                } else {
                    view.visibility = View.VISIBLE
                    (view as TextView).text = valueS
                }
                res = true
            }
            R.id.comlst_rating -> {
                val ratingBar = view as RatingBar
                ratingBar.rating = cursor.getInt(columnIndex).toFloat()
                res = true
            }
            R.id.comlst_datum -> {
                val value = cursor.getLong(columnIndex)
                (view as TextView).text = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(value))
                res = true
            }
        }
        return res
    }
}
