package info.mx.core.util

import android.location.Location
import info.mx.core.MxCoreApplication.Companion.isAdmin
import info.mx.core.common.getLocationFromCountryList
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
        get() = mxDatabase.countryDao().isShown("US") // or appropriate query

    fun hideAmerica() {
        Timber.d("LocationHelper")
        mxDatabase.runInTransaction {
            mxDatabase.countryDao().all.forEach { country ->
                val countryLocation = country.country.getLocationFromCountryList()
                when {
                    countryLocation.latitude + countryLocation.longitude == 0.0 -> country.show = if (isAdmin) 1 else 0
                    countryLocation.isUSA() -> country.show = 0
                    else -> country.show = 1
                }
                mxDatabase.countryDao().update(country)
            }
        }
    }

    fun hideEurope() {
        Timber.d("LocationHelper")
        mxDatabase.runInTransaction {
            mxDatabase.countryDao().all.forEach { country ->
                val countryLocation = country.country.getLocationFromCountryList()
                if (countryLocation.latitude + countryLocation.longitude == 0.0) {
                    country.show = if (isAdmin) 1 else 0
                } else if (countryLocation.isEurope()) {
                    country.show = 0
                } else {
                    country.show = 1
                }
                mxDatabase.countryDao().update(country)
            }
        }
    }
}
