package info.mx.tracks.ops

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import info.mx.comlib.retrofit.CommApiClient
import timber.log.Timber
import java.io.IOException

object ImportHelper {

    fun getShortCountryCoder(latitude: Double, longitude: Double, context: Context): String? {
        var countryKZ: String? = null
        val coder = Geocoder(context)
        try {
            val addresses = coder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                countryKZ = address.countryCode
            }
        } catch (e1: IOException) {
            Timber.e(e1)
        }

        return countryKZ
    }

    private fun getLatLngFromToken(apiClient: CommApiClient, token: String): LatLng? {
        var latlng: LatLng? = null
        val call = apiClient.googleService.getLatLon(token)

        try {
            val response = call.execute()
            if (response.code() == 200) {
                if (response.body() != null &&
                    response.body()!!.results != null &&
                    response.body()!!.results?.isNotEmpty() == true &&
                    response.body()!!.results?.get(0)?.geometry != null &&
                    response.body()!!.results?.get(0)?.geometry?.location != null
                ) {
                    val location = response.body()!!.results?.get(0)?.geometry?.location
                    latlng = LatLng(location?.lat!!, location.lng!!)
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
        }

        return latlng
    }

    fun getCountryFromLatLng(apiClient: CommApiClient, latitude: Double, longitude: Double): String? {
        var countryKZ: String? = null
        val token = "json?latlng=$latitude,$longitude&sensor=false"
        val call = apiClient.googleService.getLatLon(token)

        try {
            val response = call.execute()
            if (response.code() == 200) {
                if (response.body() != null && response.body()!!.results != null) {
                    for (res in response.body()!!.results!!) {
                        for (add in res?.addressComponents!!) {
                            var typeOk = false
                            for (typ in add?.types!!) {
                                if (typ == "country") {
                                    typeOk = true
                                }
                            }
                            if (typeOk) {
                                countryKZ = add.shortName
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
        }

        return countryKZ
    }

    fun getLatLngFromCountry(apiClient: CommApiClient, countryLong: String): LatLng? {
        val token = "json?address=" + countryLong.replace(" ".toRegex(), "+") + "&sensor=false"
        return getLatLngFromToken(apiClient, token)
    }

    fun getLatLngFromAddress(apiClient: CommApiClient, address: String, city: String, stateCode: String): LatLng? {
        val token = ("json?address=" + address + "," +
                city + "," +
                stateCode +
                "&sensor=false").replace(" ".toRegex(), "+")

        return getLatLngFromToken(apiClient, token)
    }

    fun getDistance(locCalcTo: Location, lat: Double, lon: Double): Long {
        val locTo = Location("calc")
        locTo.latitude = lat
        locTo.longitude = lon
        return locCalcTo.distanceTo(locTo).toLong()
    }
}
