package info.mx.tracks.common

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationResult
import com.robotoworks.mechanoid.ops.OperationServiceListener
import info.mx.tracks.R
import info.mx.tracks.ops.OpSyncFromServerOperation
import info.mx.tracks.sqlite.MxInfoDBContract

class OperationErrorPeerCertificateServiceListener(private val context: Context) : OperationServiceListener() {

    private var lastErrorTime: Long = 0

    override fun onOperationComplete(requestId: Int, result: OperationResult) {
        if (!result.isOk && result.error != null && result.error.message != null) {
            onError(result.error.message)
        }
        if (result.isOk && result.data != null && result.data.containsKey(OpSyncFromServerOperation.COUNTRY_RESULT)) {
            val countryMsg = result.data.getString(OpSyncFromServerOperation.COUNTRY_RESULT)
            if (countryMsg != null && countryMsg != "") {
                showInfoDlg(context, context.getString(R.string.enable_europe))
            }
        }
    }

    private fun onError(error: String?) {
        if (lastErrorTime + WAIT_TO_ASK < System.currentTimeMillis()) {
            val trackCount = SQuery.newQuery().count(MxInfoDBContract.Tracks.CONTENT_URI)
            if (error!!.contains("No peer certificate")) {
                showDlg(context, context.getString(R.string.PEER_ERROR))
                lastErrorTime = System.currentTimeMillis()
            } else if (error.contains("Unable to resolve host ") && trackCount < 1000) {
                showDlg(context, context.getString(R.string.HOST_ERROR))
                lastErrorTime = System.currentTimeMillis()
            }
        }
    }

    private fun showDlg(context: Context, text: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder
            .setMessage(text)
            .setCancelable(true)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                context.startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showInfoDlg(context: Context, text: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(text)
            .setTitle(R.string.info)
            .setCancelable(true)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {
        private const val WAIT_TO_ASK = (60 * 5 * 1000).toLong() //5 minutes
    }
}
