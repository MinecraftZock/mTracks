package info.hannes.mxadmin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.widget.Toast

import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops

import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.comAdminlib.retrofit.service.model.ApproveResponse
import info.mx.comAdminlib.retrofit.service.model.Approved
import info.hannes.retrofit.service.model.StatusResponse
import info.mx.comlib.retrofit.service.data.BaseObserver
import info.mx.comlib.retrofit.service.data.BaseSingleObserver
import info.mx.tracks.common.FragmentUpDown
import info.mx.core_generated.ops.AbstractOpPostImagesOperation
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.PicturesRecord
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ActivityTrackDetail

object ApproveImageAction {
    private var selectedStatus: Int = 0

    @SuppressLint("HardwareIds")
    fun confirmPicture(context: Context, restId: Long, statusCurrent: Int, dataManagerAdmin: DataManagerAdmin) {
        val array = arrayOf<CharSequence>("Decline", "Ignore", "Accept", "Rotate", "Track open")

        val picturesRecord = SQuery.newQuery()
                .expr(MxInfoDBContract.Pictures.REST_ID, SQuery.Op.EQ, restId)
                .selectFirst<PicturesRecord>(MxInfoDBContract.Pictures.CONTENT_URI)
        val tracksRecord = SQuery.newQuery()
                .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, picturesRecord.trackRestId)
                .selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)

        val builder = AlertDialog.Builder(context)
        builder.setSingleChoiceItems(array, statusCurrent + 1) { _, arg1 -> selectedStatus = arg1 - 1 }
                .setTitle("RI:" + restId + " " + if (tracksRecord == null) "<null>" else tracksRecord.trackname)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    if (selectedStatus == 2) {
                        dataManagerAdmin.rotateImage(restId)
                                .subscribe(object : BaseObserver<StatusResponse>(context) {
                                    override fun onNext(statusResponse: StatusResponse) {
                                        // sync pictures
                                        val intentM = AbstractOpPostImagesOperation.newIntent()
                                        Ops.execute(intentM)
                                        Toast.makeText(context, statusResponse.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                    } else if (selectedStatus == 3) {
                        val intent = Intent(context, ActivityTrackDetail::class.java)
                        val bundle = Bundle()
                        bundle.putLong(FragmentUpDown.RECORD_ID_LOCAL, tracksRecord!!.id)
                        bundle.putString(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Tracksges.CONTENT_URI.toString())
                        bundle.putInt(FragmentUpDown.CURSOR_POSITION, 0)
                        intent.putExtras(bundle)

                        context.startActivity(intent)
                    } else {
                        val approve = Approved()
                        approve.id = restId
                        approve.changeuser = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
                        approve.status = selectedStatus
                        dataManagerAdmin.approvePicture(approve).subscribe(object : BaseSingleObserver<ApproveResponse>(context) {
                            override fun onSuccess(result: ApproveResponse) {
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }

                        })
                    }
                }
        builder.show()

    }
}
