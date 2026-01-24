package info.mx.tracks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import info.mx.tracks.sqlite.MxInfoDBContract.Pictures
import info.mx.tracks.sqlite.PicturesRecord
import java.io.File

/**
 * Legacy BroadcastReceiver for handling low storage situations.
 *
 * Note: Intent.ACTION_DEVICE_STORAGE_LOW was deprecated in API 26 (Android 8.0).
 * This receiver is kept for backward compatibility but is no longer actively used.
 * Modern apps should implement proactive storage management instead of relying on system broadcasts.
 *
 * @deprecated This class relies on deprecated Android APIs and is no longer used in the app.
 */
@Deprecated("ACTION_DEVICE_STORAGE_LOW is deprecated since API 26")
@Suppress("DEPRECATION")
class DiskReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action != null && action == Intent.ACTION_DEVICE_STORAGE_LOW) {
            val records = SQuery.newQuery().expr(Pictures.LOCALFILE, Op.NEQ, "")
                    .select<PicturesRecord>(Pictures.CONTENT_URI, Pictures._ID)
            for (rec in records) {
                val file = File(rec.localfile)
                if (file.lastModified() < System.currentTimeMillis() - 1000 * 60 * 60 * 12) {
                    file.delete()
                }
            }
        }
    }
}
