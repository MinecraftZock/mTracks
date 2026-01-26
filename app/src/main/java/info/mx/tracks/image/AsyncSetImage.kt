package info.mx.tracks.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import timber.log.Timber
import java.io.IOException
import java.net.URL

class AsyncSetImage(private val imageView: ImageView) : AsyncTask<String?, Int?, Boolean?>() {
    private var bitmap: Bitmap? = null

    init {
        imageView.tag = true
    }

    override fun doInBackground(vararg params: String?): Boolean {
        try {
            val url = URL(params[0])
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            return true
        } catch (e: IOException) {
            Timber.e(e)
        }
        return false
    }

    override fun onPostExecute(result: Boolean?) {
        if (result == true) {
            imageView.setImageBitmap(bitmap)
        }
    }
}
