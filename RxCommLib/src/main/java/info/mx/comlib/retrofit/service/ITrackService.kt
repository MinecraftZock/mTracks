package info.mx.comlib.retrofit.service

import info.mx.comAdminlib.retrofit.service.model.ApproveResponse
import info.mx.comAdminlib.retrofit.service.model.Approved
import info.mx.comlib.retrofit.service.model.PictureR
import info.mx.comlib.retrofit.service.model.RatingR
import info.mx.comlib.retrofit.service.model.SerieR
import info.mx.comlib.retrofit.service.model.TrackBody
import info.mx.comlib.retrofit.service.model.TrackR
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Single
import retrofit2.http.GET

interface ITrackService {

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/tracks/changed/{from}")
    fun getTracksFrom(
            @Path(value = "from") from: Long,
            @Header("Authorization") authorization: String): Call<List<TrackR>>

    @Headers("Accept: application/json")
    @POST("/backend/MXServer/rest/tracks/change")
    fun getTracks(
        @Body body: TrackBody,
        @Header("Authorization") authorization: String): Call<List<TrackR>>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/series/changed/{from}")
    fun getSeries(
            @Path(value = "from") from: Long,
            @Header("Authorization") authorization: String): Call<List<SerieR>>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/pictures/changed/{from}")
    fun getPictures(
            @Path(value = "from") from: Long,
            @Header("Authorization") authorization: String): Call<List<PictureR>>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/ratings/changed/{from}")
    fun getRatings(
            @Path(value = "from") from: Long,
            @Header("Authorization") authorization: String): Call<List<RatingR>>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/ratings/track/{trackId}")
    fun getRatingsForTrack(
            @Path(value = "trackId") trackId: Long,
            @Header("Authorization") authorization: String): Single<List<RatingR>>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/weather/forTrack/{trackid}/{unit}/{lang}")
    fun getWeather4TrackSync(
            @Path(value = "trackid") trackid: Long,
            @Path(value = "unit") unit: String,
            @Path(value = "lang") lang: String,
            @Header("Authorization") authorization: String): Call<String>

    @Headers("Accept: application/json")
    @GET("/backend/MXServer/rest/tracks/changed/{from}")
    fun getTracksFromAsync(
            @Path(value = "from") from: Long,
            @Header("Authorization") authorization: String): Observable<List<TrackR>>

    // TODO move to admin
    @POST("/backend/MXServer/rest/tracks/approved")
    @Headers("Accept: application/json")
    fun approveTrack(
            @Body approve: Approved,
            @Header("Authorization") authorization: String): Call<ApproveResponse>

}
