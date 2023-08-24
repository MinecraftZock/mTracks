package info.mx.tracks.common

import com.robotoworks.mechanoid.db.SQuery

import info.mx.tracks.sqlite.ImportstatusRecord
import info.mx.tracks.sqlite.MxInfoDBContract.Importstatus

object LoggingHelper {
    fun setMessage(msg: String?) {
        var rec: ImportstatusRecord?
        rec = SQuery.newQuery().selectFirst(Importstatus.CONTENT_URI)
        if (rec == null) {
            rec = ImportstatusRecord()
        }
        rec.created = System.currentTimeMillis() / 1000
        rec.msg = msg
        rec.save(true)
    }
}
