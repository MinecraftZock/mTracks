package info.mx.tracks.tools

import android.app.Activity
import android.content.Context

@Suppress("UNUSED_PARAMETER")
class AddMobHelper(context: Context) : IAddMobHelper {

    override fun onCreateView(activity: Activity) = Unit

    override fun onPause(activity: Activity) = Unit

    override fun onResume(activity: Activity) = Unit

    override fun onDestroy(activity: Activity) = Unit

    override fun showInterstitial(activity: Activity) = Unit

}
