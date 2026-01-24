package info.mx.tracks.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import info.mx.tracks.service.LocationJobService.Companion.scheduleJob
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("boot received, starting BootReceiver")
        scheduleJob(context)
    }
}
