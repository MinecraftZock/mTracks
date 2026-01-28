package info.mx.core.common

import info.mx.tracks.ops.OpSyncFromServerOperation

object LoggingHelper {
    fun setMessage(msg: String) {
        val message = ImportStatusMessage(message = msg)
        OpSyncFromServerOperation.importStatusMessage.postValue(message)
    }
}
