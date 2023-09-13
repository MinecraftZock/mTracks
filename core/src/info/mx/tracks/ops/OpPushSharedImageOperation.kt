package info.mx.tracks.ops

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.utils.FileHelper.isImageFromSignatureIdFromHeader
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.util.RetroFileHelper
import info.mx.tracks.MxCoreApplication.Companion.getFilesDir
import info.mx.tracks.koin.CoreKoinComponent
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.sqlite.PicturesRecord
import org.koin.core.component.inject
import timber.log.Timber
import java.io.*
import java.util.*

internal class OpPushSharedImageOperation : AbstractOpPushSharedImageOperation(), CoreKoinComponent {

    private val commApiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        try {
            var toTransfer = false
            var localCopy = File(
                getFilesDir(context.applicationContext) +
                        File.separatorChar + "id" + args.trackRestId + "_" + UUID.randomUUID() + ".jpg"
            )
            Timber.d("new local file:%s", localCopy.absolutePath)
            if (args.uri.startsWith("content")) { // copy to local storage
                val uri = Uri.parse(args.uri)

                //convert uri to stream, bitmap and file
                val inStream = context.applicationContext.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inStream)
                val os: OutputStream = BufferedOutputStream(FileOutputStream(localCopy))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
                toTransfer = true
            } else if (args.uri.startsWith("http")) {  // ... or download to local storage
                toTransfer =
                    RetroFileHelper.downloadFile(commApiClient, args.uri, localCopy.absolutePath)
            } else if (args.uri.startsWith("file:///")) {  // ... or download to local storage
                val uri = Uri.parse(args.uri)
                val realPath = uri.path
                localCopy = File(realPath.toString())
                toTransfer = true
            } else {
                Timber.e("no image%s", args.uri)
                Toast.makeText(
                    context.applicationContext,
                    "no image " + args.uri,
                    Toast.LENGTH_SHORT
                ).show()
            }
            var fis: FileInputStream? = null
            if (toTransfer) {
                try {
                    fis = FileInputStream(localCopy)
                    toTransfer = isImageFromSignatureIdFromHeader(fis)
                } catch (e: IOException) {
                    Timber.e(e)
                } finally {
                    try {
                        fis?.close()
                    } catch (ex: IOException) {
                        Timber.e(ex)
                    }
                }
            }
            if (toTransfer) {
                val picRecord = PicturesRecord()
                picRecord.localfile = localCopy.absolutePath
                picRecord.username = MxPreferences.getInstance().username
                picRecord.trackRestId = args.trackRestId
                picRecord.save()
                val intentM: Intent = AbstractOpPostImagesOperation.newIntent()
                Ops.execute(intentM)
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
        return OperationResult.ok(Bundle())
    }
}