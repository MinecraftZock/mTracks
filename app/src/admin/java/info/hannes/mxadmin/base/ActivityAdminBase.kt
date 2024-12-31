package info.hannes.mxadmin.base

import android.os.Bundle
import com.robotoworks.mechanoid.ops.OperationServiceListener
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.MxCoreApplication.Companion.trackActivity
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.common.OperationErrorPeerCertificateServiceListener

abstract class ActivityAdminBase : ActivityRx() {
    private var mOperationListener: OperationServiceListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackActivity(javaClass.simpleName)
        mOperationListener = OperationErrorPeerCertificateServiceListener(this)
    }

    /**
     * Sets up the graphCore
     *
     * @param graph [AppGraph]
     */
    override fun onResume() {
        super.onResume()
        Ops.bindListener(mOperationListener)
    }

    override fun onPause() {
        super.onPause()
        Ops.unbindListener(mOperationListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (ignored: Exception) {
        }
    }
}
