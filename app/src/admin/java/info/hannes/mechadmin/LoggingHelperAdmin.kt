package info.hannes.mechadmin

import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mechadmin_gen.sqlite.ImportstatusCalRecord
import info.hannes.mechadmin_gen.sqlite.MxCalContract

object LoggingHelperAdmin {
    @JvmStatic
    fun setMessage(msg: String?) {
        var rec = SQuery.newQuery()
            .selectFirst<ImportstatusCalRecord>(MxCalContract.ImportstatusCal.CONTENT_URI)
        if (rec == null) {
            rec = ImportstatusCalRecord()
        }
        rec.created = System.currentTimeMillis() / 1000
        rec.msg = msg
        rec.save()
    }
}