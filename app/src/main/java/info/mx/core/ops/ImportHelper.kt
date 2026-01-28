package info.mx.core.ops

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import info.mx.comlib.retrofit.CommApiClient
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object ImportHelper {

    /**
     * Gets addresses from location using modern API (Android 13+) or legacy API (older versions).
     * This method blocks until addresses are retrieved or timeout occurs.
     */
    private fun getAddressesFromLocation(geocoder: Geocoder, latitude: Double, longitude: Double, @Suppress("SameParameterValue") maxResults: Int): List<Address>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Modern API (Android 13+)
            val latch = CountDownLatch(1)
            var addresses: List<Address>? = null

            geocoder.getFromLocation(latitude, longitude, maxResults) { result ->
                addresses = result
                latch.countDown()
            }

            // Wait up to 5 seconds for the result
            latch.await(5, TimeUnit.SECONDS)
            addresses
        } else {
            // Legacy API (Android 12 and below)
            @Suppress("DEPRECATION")
            try {
                geocoder.getFromLocation(latitude, longitude, maxResults)
            } catch (e: IOException) {
                Timber.e(e, "Error getting addresses from location")
                null
            }
        }
    }

    fun getShortCountryCoder(latitude: Double, longitude: Double, context: Context): String? {
        var countryKZ: String? = null
        val coder = Geocoder(context)
        try {
            val addresses = getAddressesFromLocation(coder, latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                countryKZ = address.countryCode
            }
        } catch (e1: IOException) {
            Timber.e(e1)
        }

        return countryKZ
    }

    // ...existing code...

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
