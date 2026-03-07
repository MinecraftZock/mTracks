package info.mx.core.util

import android.content.Context
import android.location.Location
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core.MxCoreApplication.Companion.isAdmin
import info.mx.core.common.getLocationFromCountryList
import info.mx.core_generated.sqlite.CountryRecord
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.tracks.room.MxDatabase
import timber.log.Timber

fun Location.isUSA(): Boolean {
    return this.longitude > -179 && this.longitude < -31
}

fun Location.isEurope(isEmulator: Boolean = false): Boolean {
//    isEmulator = MxCoreApplication.isEmulator
//    Timber.d("(this.longitude > -31 && this.longitude < 65) longitude=${this.longitude}")
//    MxCoreApplication.isEmulator ||
    return this.longitude > -31 && this.longitude < 65
}

class LocationHelper(private val mxDatabase: MxDatabase) {

    val isAmericaShown: Boolean
        get() = SQuery.newQuery()
            .expr(MxInfoDBContract.Country.COUNTRY, SQuery.Op.EQ, "US")
            .expr(MxInfoDBContract.Country.SHOW, SQuery.Op.EQ, true)
            .count(MxInfoDBContract.Country.CONTENT_URI) == 1

    fun hideAmerica(context: Context) {
        Timber.d("LocationHelper")
        val countries = SQuery.newQuery().select<CountryRecord>(MxInfoDBContract.Country.CONTENT_URI)
        for (country in countries) {
            val countryLocation = country.country.getLocationFromCountryList()
            when {
                countryLocation.latitude + countryLocation.longitude == 0.0 -> country.show = if (isAdmin) 1 else 0.toLong()
                countryLocation.isUSA() -> country.show = 0
                else -> country.show = 1
            }
            country.save(false)
        }
        context.contentResolver.notifyChange(MxInfoDBContract.Tracks.CONTENT_URI, null)
    }

    fun hideEurope(context: Context) {
        Timber.d("LocationHelper")
        val countries = SQuery.newQuery().select<CountryRecord>(MxInfoDBContract.Country.CONTENT_URI)
        for (country in countries) {
            val countryLocation = country.country.getLocationFromCountryList()
            if (countryLocation.latitude + countryLocation.longitude == 0.0) {
                country.show = (if (isAdmin) 1 else 0).toLong()
            } else if (countryLocation.isEurope()) {
                country.show = 0
            } else {
                country.show = 1
            }
            country.save(false)
        }
        context.contentResolver.notifyChange(MxInfoDBContract.Tracks.CONTENT_URI, null)
    }
}
