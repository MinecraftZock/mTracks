package info.mx.tracks.settings

import android.os.Bundle
import info.hannes.changelog.LollipopFixedWebView
import info.mx.tracks.ActivityBase
import info.mx.tracks.R
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

class ActivityAcknowledgement : ActivityBase() {

    private val contentString: String
        get() {
            var inputStream: InputStream? = null
            var content = ""
            try {
                inputStream = assets.open("open_source_acknowledgements.txt")
                val size = inputStream.available()

                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                content = String(buffer)
            } catch (e: IOException) {
                Timber.e(e)
            } finally {
                inputStream?.let {
                    try {
                        it.close()
                    } catch (e: IOException) {
                        Timber.e(e)
                    }
                }
            }

            return content
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acknowledgement)
        setWebContent()
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    private fun setWebContent() {
        val webView = findViewById<LollipopFixedWebView>(R.id.acknowledgementWebView)
        webView.setBackgroundColor(COLOR_TRANSPARENT)
        webView.loadData("<font color='white'>$contentString", "text/html", "UTF-8")
    }

    companion object {
        private const val COLOR_TRANSPARENT = 0
    }
}
