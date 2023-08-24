package info.mx.comlib.retrofit.service

import info.mx.comlib.retrofit.service.model.InsertResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IPictureService {
    @GET("/backend/MXServer/rest/pictures/img/{size}/{id}")
    fun getPicture(
        @Path(value = "size") size: Int,
        @Path(value = "id") id: Long,
        @Header("Authorization") authorization: String
    ): Call<ResponseBody>

    @Multipart
    @POST("/backend/MXServer/rest/pictures/upload")
    fun postPicture(
        @Part imageFile: MultipartBody.Part,
        @Part(value = "comment") comment: String?,
        @Part(value = "trackid") trackid: Long,
        @Part(value = "username") username: String?,
        @Part(value = "androidid") androidid: String?,
        @Header("Authorization") authorization: String
    ): Call<InsertResponse>

    fun downloadFileWithDynamicUrl(@Url fileUrl: String): Call<ResponseBody>
}
