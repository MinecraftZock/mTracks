package info.hannes.mechadmin

import info.mx.tracks.common.ImportStatusMessage
import info.mx.tracks.ops.OpSyncFromServerOperation

object LoggingHelperAdmin {
    @JvmStatic
    fun setMessage(msg: String) {
        val message = ImportStatusMessage(message = msg)
        OpSyncFromServerOperation.adminImportStatusCalMessage.postValue(message)
    }
}