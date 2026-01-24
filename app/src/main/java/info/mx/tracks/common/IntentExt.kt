package info.mx.tracks.common

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.parcelableExtra(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> getParcelableExtra(key) as? T
}

@Suppress("DEPRECATION")
inline fun <reified T : Parcelable> Intent.parcelableArrayListExtra(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> getParcelableArrayListExtra<T>(key)
}
