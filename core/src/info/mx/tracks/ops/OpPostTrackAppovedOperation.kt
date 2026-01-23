package info.mx.tracks.ops

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.Toast
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.mx.comAdminlib.retrofit.service.model.ApproveResponse
import info.mx.comAdminlib.retrofit.service.model.Approved
import info.mx.tracks.data.DataManagerCore
import info.mx.tracks.koin.CoreKoinComponent
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord
import org.koin.core.component.inject
import retrofit2.Response
import timber.log.Timber

//TODO move to Admin
internal class OpPostTrackAppovedOperation : AbstractOpPostTrackAppovedOperation(), CoreKoinComponent {

    private val dataManagerCore: DataManagerCore by inject()

    @SuppressLint("HardwareIds")
    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        return try {
            val approve = Approved()
            approve.changeuser =
                Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
            approve.status = args.status
            approve.id = args.trackId
            val call: Response<ApproveResponse> = dataManagerCore.approveTrack(approve)
            if (call.code() == com.robotoworks.mechanoid.net.Response.HTTP_OK && call.body() != null) {
                val rec = SQuery.newQuery()
                    .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, approve.id)
                    .selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)
                if (rec != null && approve.status == call.body()!!.status) {
                    rec.approved = approve.status.toLong()
                    rec.save()
                }
                OperationResult.ok(bundle)
            } else {
                OperationResult.error(Exception("approveTrack() return code " + call.code()))
            }
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(context.applicationContext, e.message, Toast.LENGTH_LONG).show()
            OperationResult.error(e)
        }
    }
}
