package info.mx.core.util

import info.mx.tracks.BuildConfig
import info.mx.core.MxCoreApplication

object Wait {

    fun delay() {
        if (MxCoreApplication.isProduction) {
            try {
                Thread.sleep(BuildConfig.PUSH_SERVER_SLEEP.toLong())
            } catch (_: InterruptedException) {
            }

        }
    }

}
