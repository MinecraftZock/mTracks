package info.mx.tracks.map

import info.mx.tracks.BuildConfig

object MapRenderingMode {
    var screenshotMode = false
        set(value) {
            check(!value || BuildConfig.DEBUG) { "Screenshot map rendering is only available in debug builds" }
            field = value
        }
}
