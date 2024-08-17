package info.hannes.commonlib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewConfiguration

/**
 * On some Samsung devices, a hardware options button is present and therefore a overflow button in Actionbar would not be shown. To force
 * showing of the overflow button, configuration needs to be changed
 */
@SuppressLint("SoonBlockedPrivateApi")
fun Context.setPhoneHasNoOptionsBtn() {
    try {
        val config = ViewConfiguration.get(this)
        val menuKeyField = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
        menuKeyField.isAccessible = true
        menuKeyField.setBoolean(config, false)
    } catch (ignored: Exception) {
    }
}
