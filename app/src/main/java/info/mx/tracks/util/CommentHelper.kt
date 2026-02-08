package info.mx.tracks.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.provider.Settings
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core.MxCoreApplication.Companion.doSync
import info.mx.core_generated.prefs.MxPreferences.Companion.instance
import info.mx.core_generated.sqlite.MxInfoDBContract.Ratings
import info.mx.core_generated.sqlite.RatingsRecord
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import java.util.Locale

object CommentHelper {
    private var ratingsRecord: RatingsRecord? = null

    // on add eventId is not used
    fun doAddRating(activity: Activity, trackRec: TracksRecord) {
        doEditRating(activity, 0, trackRec)
    }

    private fun doEditRating(activity: Activity, ratingId: Int, trackRec: TracksRecord) {
        var edtComment: EditText? = null
        var userName: EditText? = null
        var ratingBar: RatingBar? = null

        ratingsRecord = RatingsRecord.get(ratingId.toLong())
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.newRating))

        builder.setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int -> }

        builder.setPositiveButton(R.string.done) { _: DialogInterface?, _: Int ->
            val alreadyExist = SQuery.newQuery()
                .expr(Ratings.TRACK_REST_ID, SQuery.Op.EQ, trackRec.restId)
                .expr(Ratings.ANDROIDID, SQuery.Op.EQ, getAndroidID(activity))
                .expr(Ratings.NOTE, SQuery.Op.EQ, edtComment!!.text.toString())
                .exists(Ratings.CONTENT_URI)
            if (!alreadyExist) {
                if (ratingsRecord == null) {
                    ratingsRecord = RatingsRecord()
                }

                ratingsRecord!!.setTrackRestId(trackRec.restId)
                ratingsRecord!!.setApproved(0)
                ratingsRecord!!.setRating(ratingBar!!.rating.toLong())
                ratingsRecord!!.setUsername(userName!!.text.toString())
                ratingsRecord!!.setNote(edtComment!!.text.toString())
                ratingsRecord!!.setCountry(Locale.getDefault().country)
                ratingsRecord!!.setChanged(System.currentTimeMillis())
                ratingsRecord!!.setAndroidid(getAndroidID(activity))
                ratingsRecord!!.save()
                doSync(true, true, BuildConfig.FLAVOR)
                if (ratingsRecord!!.username != activity.getString(R.string.noname)) {
                    val prefs1 = instance
                    prefs1.edit().putUsername(ratingsRecord!!.username.trim { it <= ' ' })
                        .commit()
                }
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.comment_alredy_exists),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val dlg = builder.create()
        val inflater = dlg.layoutInflater
        @SuppressLint("InflateParams") val view =
            inflater.inflate(R.layout.layout_comment_add, null)
        edtComment = view.findViewById<EditText>(R.id.com_note)
        ratingBar = view.findViewById<RatingBar>(R.id.com_rating)
        if (ratingsRecord != null) {
            edtComment!!.setText(ratingsRecord!!.note)
        }

        val prefs = instance
        userName = view.findViewById<EditText>(R.id.com_username).apply {
            setHint(R.string.default_username)
            setText(prefs.username)
        }
        dlg.setView(view)
        dlg.show()
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidID(activity: Activity): String? {
        return Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
