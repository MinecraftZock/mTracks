package info.mx.comlib.retrofit.service

import info.mx.comlib.retrofit.service.model.google.GeoCode
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface IGoogleService {
    @GET("/maps/api/geocode/json")
    @Headers("Accept: application/json")
    fun getLatLon(
        @Query("address") address: String
    ): Call<GeoCode>
}
