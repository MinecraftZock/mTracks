package info.mx.tracks

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.robotoworks.mechanoid.ops.OperationServiceListener
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.common.OperationErrorPeerCertificateServiceListener
import info.mx.tracks.tools.AddMobHelper
import info.mx.tracks.tools.PermissionHelper
import org.koin.android.ext.android.inject

abstract class ActivityBase : ActivityRx() {

    val permissionHelper: PermissionHelper by inject()

    private val addMobHelper: AddMobHelper by inject()

    private var operationListener: OperationServiceListener? = null
    private val appPROPackageName = "info.mx.tracks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MxCoreApplication.trackActivity(javaClass.simpleName)

        operationListener = OperationErrorPeerCertificateServiceListener(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        addMobHelper.onCreateView(this)
    }

    override fun onResume() {
        super.onResume()
        Ops.bindListener(operationListener)
        addMobHelper.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        Ops.unbindListener(operationListener)
        addMobHelper.onPause(this)
    }

    public override fun onDestroy() {
        addMobHelper.onDestroy(this)
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (ignored: Exception) {
        }

    }

    protected fun openPermission() {
        permissionHelper.goToSettings()
    }

    protected fun showProVersionDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setTitle("PRO Version")
            .setMessage(getString(R.string.show_pro_dialog))
            .setCancelable(true)
            .setNegativeButton(R.string.jump_google_play_store) { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPROPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$appPROPackageName")))
                }
            }
            .setPositiveButton(android.R.string.ok, null)
        val alertDialog = alertDialogBuilder.create()
        //        alertDialog.getWindow().setLayout(AdapterImageUris.getDesiredScreenWidth(), AdapterImageUris.getDesiredScreenHeight());
        alertDialog.show()
    }
}
