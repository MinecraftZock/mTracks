package info.mx.tracks.tools

import android.app.Activity

interface IAddMobHelper {
    fun onCreateView(activity: Activity)

    fun onPause(activity: Activity)

    fun onResume(activity: Activity)

    fun onDestroy(activity: Activity)

    fun showInterstitial(activity: Activity)
}
