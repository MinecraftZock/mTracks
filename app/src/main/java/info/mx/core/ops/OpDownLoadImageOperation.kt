package info.mx.core.ops

import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.commonlib.NetworkHelper.isOnline
import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import info.mx.core.MxCoreApplication.Companion.getFilesDir
import info.mx.core.koin.CoreKoinComponent
import info.mx.core_generated.ops.AbstractOpDownLoadImageOperation
import info.mx.core_generated.sqlite.PicturesRecord
import org.apache.commons.io.IOUtils
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OpDownLoadImageOperation : AbstractOpDownLoadImageOperation(), CoreKoinComponent {

    private val commApiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val picture = PicturesRecord.get(args.pictureId)
            ?: return OperationResult.error(Exception("picture not found " + args.pictureId))
        return try {
            val file = File(
                getFilesDir(context.applicationContext) +
                        File.separatorChar + "id" + picture.id + "_" + args.size + ".png"
            )
            if (isOnline(context.applicationContext)) {
                if (downloadRetroFit(picture.restId, args.size, file.absolutePath)) {
                    if (args.thumb) {
                        picture.localthumb = file.absolutePath
                    } else {
                        picture.localfile = file.absolutePath
                    }
                    picture.save()
                }
            }
            OperationResult.ok()
        } catch (e: Exception) {
            OperationResult.error(e)
        }
    }

    private fun downloadRetroFit(id: Long, size: Int, fileTo: String): Boolean {
        var res = false
        val basic = aadhresUBase
        val call = commApiClient.pictureService.getPicture(size, id, basic)
        try {
            val response = call.execute()
            try {
                if (response.code() == 200) {
                    val file = File(fileTo)
                    val fileOutputStream = FileOutputStream(file)
                    Timber.w("body=${response.body().toString()}")
                    IOUtils.write(response.body()!!.bytes(), fileOutputStream)
                    res = true
                }
            } catch (e: IOException) {
                Timber.e(e)
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
        return res
    }
}
