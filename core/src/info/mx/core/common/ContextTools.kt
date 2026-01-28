package info.mx.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun Context.getAndroidId(): String {
    return Settings.Secure.getString(
        this.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}
