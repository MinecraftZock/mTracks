package info.mx.tracks.data

import info.mx.comAdminlib.retrofit.service.model.ApproveResponse
import info.mx.comAdminlib.retrofit.service.model.Approved
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.retrofit.service.model.*
import retrofit2.Response
import java.io.IOException

/**
 * It keeps a reference to every helper class and uses them to satisfy the requests coming from the UI.
 * Its methods make extensive use of Rx operators to combine, transform or filter the output coming from the helpers in order to generate the
 * desired output ready for the UI.
 * It returns observables that emit data models.
 */
open class DataManagerCore(protected val commApiClient: CommApiClient) : DataManagerBase() {

    @Throws(IOException::class)
    fun getTracks(
        fromDate: Int,
        androidid: String,
        appversion: Long,
        androidversion: Int,
        country: String,
        flavor: String,
        ip: String
    ): Response<List<TrackR>> {
        val trackBody = TrackBody()
        trackBody.changed = fromDate
        trackBody.androidversion = androidversion
        trackBody.androidid = androidid
        trackBody.appversion = appversion.toInt()
        trackBody.country = country
        trackBody.flavor = flavor
        trackBody.setIp(ip)
        return commApiClient.tracksService.getTracks(trackBody, basic).execute()
    }

    @Throws(IOException::class)
    fun getRatings(fromDate: Long): Response<List<RatingR>> {
        return commApiClient.tracksService.getRatings(fromDate, basic).execute()
    }

    @Throws(IOException::class)
    fun getPictures(fromDate: Long): Response<List<PictureR>> {
        return commApiClient.tracksService.getPictures(fromDate, basic).execute()
    }

    @Throws(IOException::class)
    fun getSeries(fromDate: Long): Response<List<SerieR>> {
        return commApiClient.tracksService.getSeries(fromDate, basic).execute()
    }

    @Throws(IOException::class)
    fun getWeather4Track(trackRestID: Long, unit: String, lang: String): Response<String> {
        return commApiClient.tracksService.getWeather4Track(trackRestID, unit, lang, basic).execute()
    }

    @Throws(IOException::class)
    fun approveTrack(approved: Approved): Response<ApproveResponse> {
        return commApiClient.tracksService
            .approveTrack(approved, basic).execute()
    }

}
