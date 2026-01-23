package info.hannes.mxadmin.picture

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView

import java.io.File

import info.hannes.mechadminGen.sqlite.PictureStageRecord
import info.mx.tracks.R
import info.mx.tracks.prefs.MxPreferences
import timber.log.Timber

internal object PictureAdminHelper {

    fun checkAndSetImage(context: Context, record: PictureStageRecord, imageView: ImageView, wishSize: Int): Boolean {
        var res = false
        val thumb = wishSize == Math.round(context.resources.getDimension(R.dimen.thumbnail_size_dp))
        val prefs = MxPreferences.getInstance()
        var size = wishSize

        // crop image if too big
        if (!thumb && prefs.imageCropTo > 0 && size > prefs.imageCropTo) {
            size = prefs.imageCropTo
        }

        var filepathLocal = record.localFile
        val imgFile = File(filepathLocal)

        // correct it, if not available (=error)
        if (!imgFile.exists() && filepathLocal != "") {
            Timber.e("File missing %s", filepathLocal)
            record.localFile = ""
            record.save()
            filepathLocal = ""
        }

        if (filepathLocal != "") {
            val file2Set = File(filepathLocal)
            if (!file2Set.exists()) {
                Timber.w("not exists %s", filepathLocal)
            } else {
                val drawable = Drawable.createFromPath(filepathLocal)
                imageView.setImageDrawable(drawable)
                Timber.d("Id:${record.id} size:$size h:${imageView.height} w:${imageView.width}")
                res = true
            }
        }
        return res
    }

}
