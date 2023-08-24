package info.mx.tracks.util

import info.mx.tracks.BuildConfig
import info.mx.tracks.MxCoreApplication

object Wait {

    fun delay() {
        if (MxCoreApplication.isProduction) {
            try {
                Thread.sleep(BuildConfig.PUSH_SERVER_SLEEP.toLong())
            } catch (ignored: InterruptedException) {
            }

        }
    }

}
