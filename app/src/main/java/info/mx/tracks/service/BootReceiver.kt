package info.mx.tracks.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import info.mx.tracks.service.LocationJobService.Companion.scheduleJob
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Validate that the intent action matches the expected boot action
        // to prevent spoofed intents from triggering undesired behavior
        if (intent == null) {
            Timber.w("Received null intent, ignoring")
            return
        }

        when (intent.getAction()) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                Timber.d("boot received (action: ${intent.getAction()}), starting BootReceiver")
                scheduleJob(context)
            }
            else -> {
                Timber.w("Received unexpected action: ${intent.getAction()}, ignoring")
            }
        }
    }
}
