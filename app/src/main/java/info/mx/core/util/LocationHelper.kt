package info.mx.core.util

import android.content.Context
import android.location.Location
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core.MxCoreApplication
import info.mx.core.MxCoreApplication.Companion.isAdmin
import info.mx.core.common.CountryTools
import info.mx.core_generated.sqlite.CountryRecord
import info.mx.core_generated.sqlite.MxInfoDBContract
import timber.log.Timber

fun Location.isUSA(): Boolean {
    return this.longitude > -179 && this.longitude < -31
}

fun Location.isEurope(): Boolean {
    Timber.d("isEurope=${MxCoreApplication.isEmulator || (this.longitude > -31 && this.longitude < 65)} longitude=${this.longitude}")
    return MxCoreApplication.isEmulator || (this.longitude > -31 && this.longitude < 65)
}

object LocationHelper {
    val isAmericaShown: Boolean
        get() = SQuery.newQuery()
            .expr(MxInfoDBContract.Country.COUNTRY, SQuery.Op.EQ, "US")
            .expr(MxInfoDBContract.Country.SHOW, SQuery.Op.EQ, true)
            .count(MxInfoDBContract.Country.CONTENT_URI) == 1

    fun hideAmerica(context: Context) {
        val countries = SQuery.newQuery().select<CountryRecord>(MxInfoDBContract.Country.CONTENT_URI)
        for (country in countries) {
            val latitude = CountryTools.getLatitude(country.country)
            val longitude = CountryTools.getLongitude(country.country)
            val countryLocation = Location("country")
            countryLocation.latitude = latitude
            countryLocation.longitude = longitude
            when {
                latitude + longitude == 0.0 -> country.show = if (isAdmin) 1 else 0.toLong()
                countryLocation.isUSA() -> country.show = 0
                else -> country.show = 1
            }
            country.save(false)
        }
        context.contentResolver.notifyChange(MxInfoDBContract.Tracks.CONTENT_URI, null)
    }
}
