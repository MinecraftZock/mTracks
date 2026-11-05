package info.mx.tracks.settings

import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Country
import info.mx.tracks.room.entity.CountryCount
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CountryFilterRepository : KoinComponent {
    private val mxDatabase: MxDatabase by inject()

    fun getAllCountryCount(): Flow<List<CountryCount>> = mxDatabase.countryDao().getAllCountryCount()

    fun getAllCountries(): List<Country> = mxDatabase.countryDao().all

    fun updateCountryShow(id: Long, show: Boolean) {
        val country = mxDatabase.countryDao().byId(id)
        if (country != null) {
            country.show = if (show) 1 else 0
            mxDatabase.countryDao().update(country)
        }
    }

    fun updateAllCountriesShow(show: Boolean): Int {
        return mxDatabase.countryDao().updateShowByCountryCode(show)
    }
}


