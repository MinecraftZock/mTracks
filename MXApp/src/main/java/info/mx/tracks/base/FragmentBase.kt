package info.mx.tracks.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.tools.PermissionHelper
import org.koin.android.ext.android.inject

abstract class FragmentBase : FragmentRx() {

    val permissionHelper: PermissionHelper by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MxCoreApplication.trackActivity(javaClass.simpleName)
    }

    protected fun showInfo(activity: Activity, text: String) {
        val snackBar: Snackbar = Snackbar.make(activity.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
        snackBar.show()
    }

}
