package info.hannes.commonlib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewConfiguration

object DeviceTools {
    /**
     * On some Samsung devices, a hardware options button is present and therefore a overflow button in Actionbar would not be shown. To force
     * showing of the overflow button, configuration needs to be changed
     */
    @SuppressLint("SoonBlockedPrivateApi")
    fun setPhoneHasNoOptionsBtn(context: Context) {
        try {
            val config = ViewConfiguration.get(context)
            val menuKeyField = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            menuKeyField.isAccessible = true
            menuKeyField.setBoolean(config, false)
        } catch (ignored: Exception) {
        }
    }
}