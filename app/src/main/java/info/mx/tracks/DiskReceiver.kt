package info.mx.tracks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import info.mx.tracks.sqlite.MxInfoDBContract.Pictures
import info.mx.tracks.sqlite.PicturesRecord
import java.io.File

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
