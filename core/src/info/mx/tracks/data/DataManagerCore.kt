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
open class DataManagerCore(private val commApiClient: CommApiClient) : DataManagerBase() {

    @Throws(IOException::class)
    fun getWeather4TrackSync(trackRestID: Long, unit: String, lang: String): Response<String> {
        return commApiClient.tracksService.getWeather4Track(trackRestID, unit, lang, basic).execute()
    }

    @Throws(IOException::class)
    fun approveTrack(approved: Approved): Response<ApproveResponse> {
        return commApiClient.tracksService
            .approveTrack(approved, basic).execute()
    }

}
