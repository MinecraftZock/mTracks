package info.hannes.mechadmin_gen.ops.mxcal

import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadmin_gen.sqlite.MxCalContract.MxTrack
import info.hannes.mechadmin_gen.sqlite.MxTrackRecord
import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxCoreApplication.Companion.mxInfo
import info.mx.tracks.ops.ImportHelper.getLatLngFromAddress
import info.mx.tracks.rest.LatLng
import info.mx.tracks.rest.PostLatLngRequest
import info.mx.tracks.util.Wait.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

internal class OpMxCallFixLatLngOperation : AbstractOpMxCallFixLatLngOperation(), KoinComponent {

    val apiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val webClient = mxInfo
        return try {
            val query = SQuery.newQuery()
            if (!args.alle) {
                query.append(MxTrack.LAT + " is null")
            }
            val tracks = query.select<MxTrackRecord>(MxTrack.CONTENT_URI, MxTrack._ID)
            for (track in tracks) {
                LoggingHelperAdmin.setMessage("FixLatLng " + track.address)

                val latlng = getLatLngFromAddress(
                    apiClient, track.address, track.city, track.stateCode
                )
                if (latlng != null) {
                    val latlngParam = LatLng()
                    latlngParam.trackname = track.name
                    latlngParam.lat = latlng.latitude
                    latlngParam.lng = latlng.longitude
                    val requestLatLng = PostLatLngRequest(latlngParam)
                    webClient.postLatLng(requestLatLng)
                    delay()
                    track.lat = latlng.latitude
                    track.lng = latlng.longitude
                    track.save()
                }
            }
            LoggingHelperAdmin.setMessage("")
            val bundle = Bundle()
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            Timber.e(e)
            LoggingHelperAdmin.setMessage("")
            Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            OperationResult.error(e)
        }
    }

}
