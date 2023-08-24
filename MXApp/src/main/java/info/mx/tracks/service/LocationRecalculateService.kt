package info.mx.tracks.service

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.JobIntentService
import info.mx.tracks.ops.OpSyncFromServerOperation.Companion.LOCATION
import info.mx.tracks.ops.OpSyncFromServerOperation.Companion.SOURCE

class LocationRecalculateService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        val location: Location? = intent.getParcelableExtra(LOCATION)
        val source: String? = intent.getStringExtra(SOURCE)

        val recalculateDistance = RecalculateDistance(this)
        location?.let {
            recalculateDistance.recalculateTracks(it, "$source Service")
        }

    }

    // convenient method for starting the service.
    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, LocationRecalculateService::class.java, 1, intent)
        }
    }
}
