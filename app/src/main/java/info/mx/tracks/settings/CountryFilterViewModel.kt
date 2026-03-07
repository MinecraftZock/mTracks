package info.mx.tracks.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import info.mx.tracks.room.entity.Country
import info.mx.tracks.room.entity.CountryCount
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CountryFilterViewModel : ViewModel(), KoinComponent {
    private val repository: CountryFilterRepository by inject()

    val allCountryCount: LiveData<List<CountryCount>> = repository.getAllCountryCount().asLiveData()

    fun getAllCountries(): List<Country> = repository.getAllCountries()

    fun updateCountryShow(id: Long, show: Boolean) = viewModelScope.launch {
        repository.updateCountryShow(id, show)
    }

    fun updateAllCountriesShow(show: Boolean) = viewModelScope.launch {
        repository.updateAllCountriesShow(show)
    }
}

