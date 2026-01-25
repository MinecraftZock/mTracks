package info.mx.tracks.image

import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.URL

object AsyncSetImage {
    suspend fun loadImageIntoView(imageView: ImageView, urlString: String) {
        imageView.tag = true

        val bitmap = withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                Timber.e(e)
                null
            }
        }

        withContext(Dispatchers.Main) {
            bitmap?.let {
                imageView.setImageBitmap(it)
            }
        }
    }
}
