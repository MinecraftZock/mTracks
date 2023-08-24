package info.hannes.retrofit.service

import info.hannes.retrofit.service.model.VersionInfo
import info.mx.comlib.retrofit.service.data.RxNetworkProblem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface ISurveillanceService {

    @GET("/backend/MXServer/rest/network/changed/{last}/{limit}")
    @Headers("Accept: application/json")
    fun getNetworkProblems(
            @Path("last") lastExisting: Long,
            @Path("limit") limit: Long,
            @Header("Authorization") authorization: String): Observable<List<RxNetworkProblem>>

    @GET("/backend/MXServer/rest/version")
    @Headers("Accept: application/json")
    fun getBackendInfo(
            @Header("Authorization") authorization: String): Observable<VersionInfo>
}
