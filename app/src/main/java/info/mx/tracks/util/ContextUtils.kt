package info.mx.tracks.util

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun Context.getAndroidID(): String = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
