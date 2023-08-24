package info.mx.tracks.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("boot received, starting BootReceiver");
        LocationJobService.Companion.scheduleJob(context);
    }
}
