package info.mx.tracks.base

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
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

    protected fun showSnackbar(text: String) {
        var viewPos: View? = null//= view!!.findViewById(R.id.coordinatorLayout)
        if (viewPos == null) {
            viewPos = requireView().findViewById(android.R.id.content)
        }
        if (viewPos != null) {
            val snackbar: Snackbar = Snackbar.make(viewPos, text, Snackbar.LENGTH_LONG)
            val view = snackbar.view
            when (val params = view.layoutParams) {
                is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams -> {
                    val paramsC = view.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
                    paramsC.gravity = Gravity.CENTER_VERTICAL
                    view.layoutParams = paramsC
                    snackbar.show()
                }
                is FrameLayout.LayoutParams -> {
                    val paramsC = view.layoutParams as FrameLayout.LayoutParams
                    paramsC.gravity = Gravity.BOTTOM
                    view.layoutParams = paramsC
                    snackbar.show()
                }
                else -> {
                    Toast.makeText(context, ">" + text + "< " + params.javaClass.simpleName, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, ">$text< ", Toast.LENGTH_SHORT).show()
        }
    }

}
