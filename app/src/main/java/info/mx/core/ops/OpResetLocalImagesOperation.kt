package info.mx.core.ops

import com.robotoworks.mechanoid.db.ActiveRecord
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.core_generated.ops.AbstractOpResetLocalImagesOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.PicturesRecord
import java.io.File

class OpResetLocalImagesOperation : AbstractOpResetLocalImagesOperation() {
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val res = OperationResult(OperationResult.RESULT_OK)
        if (MxPreferences.getInstance().resetPictureStorageCount > MAX_RESET) {
            return res
        }
        for (activeRecord in SQuery.newQuery()
            .select<ActiveRecord>(MxInfoDBContract.Pictures.CONTENT_URI)) {
            val pictureRecord = activeRecord as PicturesRecord
            if (pictureRecord.localthumb != null && pictureRecord.localthumb != "") {
                val thumbFile = File(pictureRecord.localthumb)
                if (thumbFile.exists()) {
                    thumbFile.delete()
                }
                pictureRecord.localthumb = ""
                val localFile = File(pictureRecord.localfile)
                if (localFile.exists()) {
                    localFile.delete()
                }
                pictureRecord.localfile = ""
                pictureRecord.save(false)
            }
        }
        MxPreferences.getInstance().edit()
            .putResetPictureStorageCount(MxPreferences.getInstance().resetPictureStorageCount + 1)
            .apply()
        return res
    }

    companion object {
        private const val MAX_RESET = 6
    }
}
