package info.hannes.commonlib

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkHelper {

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null) {
                @Suppress("DEPRECATION")
                return networkInfo.isConnected &&
                        (networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE)
            }
        } else {
            val network = connectivityManager.activeNetwork

            if (network != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                return capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        }

        return false
    }
}
