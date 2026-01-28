package info.mx.core.ops

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.commonlib.NetworkHelper.isOnline
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.retrofit.service.model.InsertResponse
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import info.mx.core.koin.CoreKoinComponent
import info.mx.core_generated.ops.AbstractOpPostImagesOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract.Pictures
import info.mx.core_generated.sqlite.PicturesRecord
import info.mx.tracks.ops.OpSyncFromServerOperation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.IOException

class OpPostImagesOperation : AbstractOpPostImagesOperation(), CoreKoinComponent {

    private val commApiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        val prefs = MxPreferences.getInstance()
        return if (isOnline(context.applicationContext)) {
            var start: Long

            @SuppressLint("HardwareIds")
            val androidId = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
            val username = if (prefs.username == "") "unknown" else prefs.username
            try {
                val imgList = SQuery.newQuery()
                    .expr(Pictures.REST_ID, SQuery.Op.LTEQ, 0)
                    .or()
                    .append(Pictures.REST_ID + " is null")
                    .select<PicturesRecord>(Pictures.CONTENT_URI, Pictures._ID)
                for (picRecord in imgList) {
                    start = System.currentTimeMillis()
                    val recordFile = File(picRecord.localfile)
                    if (recordFile.exists()) {
                        val response = doPostImage(picRecord, username, androidId)
                        if (response!!.id > 0) {
                            picRecord.restId = response.id.toLong()
                            picRecord.changed =
                                response.changed - 1 // so wird vom server aktuallisiert
                            Timber.d("${response.id} Zeit: ${System.currentTimeMillis() - start}ms")
                            picRecord.save()
                        }
                    } else {
                        picRecord.delete()
                    }
                }
                val opPic = OpSyncFromServerOperation()
                opPic.doSyncPictures(context, true)
                OperationResult.ok(bundle)
            } catch (e: Exception) {
                val msg = if (e.message == null) "NullPointerException" else e.message!!
                Timber.e(e)
                Toast.makeText(context.applicationContext, msg, Toast.LENGTH_LONG).show()
                OperationResult.error(e)
            }
        } else {
            OperationResult.ok(bundle)
        }
    }

    private fun doPostImage(
        picRecord: PicturesRecord,
        username: String,
        androidId: String
    ): InsertResponse? {
        val start = System.currentTimeMillis()
        val basic = aadhresUBase
        val file = File(picRecord.localfile)
        val fileReqBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, fileReqBody)

        val call = commApiClient.pictureService.postPicture(
            filePart,
            picRecord.comment,
            picRecord.trackRestId,
            username,
            androidId, basic
        )
        try {
            val result = call.execute()
            Timber.d("doPostImage ${System.currentTimeMillis() - start}ms")
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }
}
