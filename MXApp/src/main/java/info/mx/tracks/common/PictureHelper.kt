package info.mx.tracks.common

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.R
import info.mx.tracks.ops.AbstractOpDownLoadImageOperation
import info.mx.tracks.ops.AbstractOpResetLocalImagesOperation
import info.mx.tracks.ops.CountingIdlingResourceSingleton
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.sqlite.PicturesRecord
import timber.log.Timber
import java.io.File
import kotlin.math.roundToInt

object PictureHelper {
    fun checkAndSetImage(context: Context, record: PicturesRecord, imgView: ImageView, localFilepath: String, wishSize: Int): Boolean {

        var filepathLocal = localFilepath
        var res = false
        val thumb = wishSize == context.resources.getDimension(R.dimen.thumbnail_size_dp).roundToInt()
        val prefs = MxPreferences.getInstance()
        var size = wishSize

        // crop image if too big
        if (!thumb && prefs.imageCropTo > 0 && size > prefs.imageCropTo) {
            size = prefs.imageCropTo
        }
        val imgFile = File(filepathLocal)

        // in case if not valid (=Error) correct it
        if (!imgFile.exists() && filepathLocal != "") {
            Timber.w("File missing %s", filepathLocal)
            if (thumb) {
                record.localthumb = ""
            } else {
                record.localfile = ""
            }
            record.save()
            filepathLocal = ""
        }
        if (filepathLocal == "") {
            Timber.d("download " + record.id + " " + record.restId + " file:" + filepathLocal)
            val intent: Intent = AbstractOpDownLoadImageOperation.newIntent(record.id, size, thumb)
            Ops.execute(intent)
        } else {
            val file2Set = File(filepathLocal)
            if (!file2Set.exists()) {
                Timber.w("not exists %s", filepathLocal)
            } else {
                //                http://developer.android.com/training/displaying-bitmaps/index.html
                //                if (imgView.getDrawable() != null) {
                //                    Bitmap mBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                //                    if (mBitmap != null && !mBitmap.isRecycled()) {
                //                        mBitmap.recycle();
                //                    }
                //                }
                //                /storage/emulated/0/Android/data/info.hannes.mxadmin/files/id298_480.png
                //                /shell/emulated/0/Android/data/info.hannes.mxadmin/files/id298_480.png <-- real
                val drawable = Drawable.createFromPath(filepathLocal)
                imgView.setImageDrawable(drawable)

                // local images seems to be corrupt
                if (drawable == null) {
                    Ops.execute(AbstractOpResetLocalImagesOperation.newIntent())
                }
                Timber.d("Id:" + record.id + " size:" + size + " h:" + imgView.height + " w:" + imgView.width)
                res = true
            }
        }
        return res
    }

    @JvmStatic
    fun getRealPathFromUri(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }
}
