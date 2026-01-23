package info.mx.tracks.util

import android.content.Context
import android.location.Location
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.MxCoreApplication.Companion.isAdmin
import info.mx.tracks.common.CountryTools
import info.mx.tracks.sqlite.CountryRecord
import info.mx.tracks.sqlite.MxInfoDBContract

object LocationHelper {
    fun isAmerica(location: Location): Boolean {
        return location.longitude > -179 && location.longitude < -31
    }

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
                isAmerica(countryLocation) -> country.show = 0
                else -> country.show = 1
            }
            country.save(false)
        }
        context.contentResolver.notifyChange(MxInfoDBContract.Tracks.CONTENT_URI, null)
    }
}
