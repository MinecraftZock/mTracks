package info.mx.tracks.ops

import android.os.Bundle
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.commonlib.NetworkHelper.isOnline
import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.koin.CoreKoinComponent
import info.mx.tracks.ops.ImportHelper.getLatLngFromCountry
import org.koin.core.component.inject
import timber.log.Timber

internal class OpGetLatLngOperation : AbstractOpGetLatLngOperation(), CoreKoinComponent {

    private val commApiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        return try {
            val bundle = Bundle()
            if (isOnline(context.applicationContext)) {
                val latlng = getLatLngFromCountry(
                    commApiClient, args.countryLong
                )
                if (latlng != null) {
                    bundle.putDouble(LAT, latlng.latitude)
                    bundle.putDouble(LNG, latlng.longitude)
                }
            }
            OperationResult.ok(bundle)
        } catch (e: Exception) {
            Timber.e(e)
            OperationResult.error(e)
        }
    }

    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
    }
}