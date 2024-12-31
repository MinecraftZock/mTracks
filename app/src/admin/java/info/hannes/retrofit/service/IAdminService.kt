package info.hannes.retrofit.service

import info.hannes.retrofit.service.model.PictureResponse
import info.hannes.retrofit.service.model.StatusResponse
import info.hannes.retrofit.service.model.Tracksarchiv
import info.mx.comAdminlib.retrofit.service.model.*
import info.mx.comlib.retrofit.service.model.InsertResponse
import info.mx.comlib.retrofit.service.model.RiderTrack
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface IAdminService {

    @Multipart
    @POST("/backend/MXServer/rest/tracksstage/uploadTrackZusatz")
    fun postZip(
            @Part imageFile: MultipartBody.Part,
            @Part(value = "urljson") urljson: String,
            @Part(value = "id") id: Long,
            @Header("Authorization") authorization: String): Call<PictureResponse>

    @Multipart
    @POST("/backend/MXServer/rest/tracksstage/uploadAllJson")
    fun postJsonZip(
            @Part imageFile: MultipartBody.Part,
            @Header("Authorization") authorization: String): Call<PictureResponse>

    @Multipart
    @POST("/backend/MXServer/rest/mxcalquellfile/uploadRiderState")
    fun postRiderState(
            @Part(value = "url") url: String,
            @Part imageFile: MultipartBody.Part,
            @Header("Authorization") authorization: String): Call<List<RiderTrack>>

    @Multipart
    @POST("/MXServer/rest/mxcalquellfile/upload")
    fun postMXCalFile(
            @Part(value = "log") log: String,
            @Part(value = "changed") changed: Long,
            @Part(value = "url") url: String,
            @Part(value = "updatedcount") updatedcount: Long,
            @Part imageFile: MultipartBody.Part,
            @Header("Authorization") authorization: String): Call<InsertResponse>

    @GET("/backend/MXServer/rest/tracksarchiv/arch/{trackID}")
    @Headers("Accept: application/json")
    fun getTracksarchiv(
            @Path("trackID") restTrackId: Long,
            @Header("Authorization") authorization: String): Observable<List<Tracksarchiv>>

    @GET("/backend/MXServer/rest/pictures/rotate/{imageID}")
    @Headers("Accept: application/json")
    fun rotateImage(
            @Path("imageID") restImageId: Long,
            @Header("Authorization") authorization: String): Observable<StatusResponse>

    @POST("/backend/MXServer/rest/pictures/approved")
    @Headers("Accept: application/json")
    fun approvePicture(
            @Body approve: Approved,
            @Header("Authorization") authorization: String): Single<ApproveResponse>

    @GET("/backend/MXServer/rest/tracksstage/decline/{id}")
    @Headers("Accept: application/json")
    fun trackStageDecline(
            @Path("id") stageId: Long,
            @Header("Authorization") authorization: String): Single<String>

    @GET("/backend/MXServer/rest/tracksstage/accept/{id}")
    @Headers("Accept: application/json")
    fun trackStageAccept(
            @Path("id") stageId: Long,
            @Header("Authorization") authorization: String): Single<String>

    @DELETE("/backend/MXServer/rest/tracksstage/{id}")
    @Headers("Accept: application/json")
    fun trackStageDelete(
            @Path("id") stageId: Long,
            @Header("Authorization") authorization: String): Single<Response<Void>>

}
