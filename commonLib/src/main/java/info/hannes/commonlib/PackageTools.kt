package info.hannes.commonlib

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * Created by Hannes Achleitner on 02.09.23.
 */
fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION")
        getPackageInfo(packageName, flags)
    }
