package info.hannes.commonlib

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import timber.log.Timber
import kotlin.math.roundToInt
import androidx.core.net.toUri

object LocationHelper {

    private const val MILES_FACTOR = 1.609344
    private const val RUNDEN_STELLEN = 200
    private const val YARD_FACTOR = 0.9144
    private const val FORMAT_THRESHOLD = 1400

    fun getFormatDistance(showKm: Boolean, meterSource: Int): String {
        var meter = meterSource
        // String res = Math.round(meter / 100) * 100 + " m";
        val res: String
        if (meter > FORMAT_THRESHOLD) {
            res = if (showKm) (meter / 1000).toFloat().roundToInt().toString() + " km"
            else
                (meter.toDouble() / MILES_FACTOR / 1000.0).roundToInt().toString() + " mi"
        } else {
            meter = (meter / RUNDEN_STELLEN).toFloat().roundToInt() * RUNDEN_STELLEN
            res = if (showKm) "$meter m" else (meter * YARD_FACTOR).roundToInt().toString() + " yd"
        }
        return res
    }

    private fun getLastKnownBestLocation(context: Context): Location {
        var lastLocation: Location? = null
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        for (provider in locationManager.allProviders) {
            if (locationManager.isProviderEnabled(provider)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                    lastLocation = locationManager.getLastKnownLocation(provider)
                }
                break
            }
        }
        if (lastLocation == null) {
            lastLocation = Location("default")
        }
        return lastLocation
    }

    fun openNavi(context: Context, name: String?, latitude: Double, longitude: Double) {
        val lastLoc = getLastKnownBestLocation(context)

        var navi = "geo:" + lastLoc.latitude + "," + lastLoc.longitude + "?q=" + latitude + "," + longitude
        Timber.d(navi)
        if (name != null) {
            navi += " ($name)"
        }
        // Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        var intent = Intent(Intent.ACTION_VIEW, navi.toUri())
        try {
            if (isDefaultNaviAppSygic(context, intent)) {
                val type = "drive"
                val str = "com.sygic.aura://coordinate|" + lastLoc.longitude + "|" + lastLoc.latitude + "|" + type
                intent = Intent(Intent.ACTION_VIEW, str.toUri())
            }
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.no_navi_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDefaultNaviAppSygic(context: Context, intent: Intent): Boolean {
        val pm = context.packageManager
        val info = pm.resolveActivity(intent, 0)
        return if (info != null && info.activityInfo.applicationInfo != null) {
            pm.getApplicationLabel(info.activityInfo.applicationInfo) == "Sygic"
        } else {
            false
        }
    }

}
