package info.hannes.mechadminGen.ops.mxcal

import android.content.ContentValues
import android.os.Bundle
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.commonlib.utils.ExternalStorage
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadminGen.sqlite.MxAdminDBContract.*
import info.hannes.mechadminGen.sqlite.PictureStageRecord
import info.hannes.mechadminGen.sqlite.TrackstageBrotherRecord
import info.hannes.mechadminGen.sqlite.VideosRecord
import info.mx.comlib.retrofit.CommApiClient
import info.mx.comlib.retrofit.service.model.InsertResponse
import info.mx.comlib.util.RetroFileHelper
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import info.mx.tracks.MxCoreApplication.Companion.mxInfo
import info.mx.tracks.common.SecHelper
import info.mx.tracks.rest.PostTrackstageIDRequest
import info.mx.tracks.rest.RESTtrackStage
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.util.Wait.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.io.IOException

internal class OpBrothersPushOperation : AbstractOpBrothersPushOperation(), KoinComponent {

    val commApiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        try {
            val webClient = mxInfo
            // wegen des hohen Speicherverbrauches des Html geht eine List<> nicht, daher ein Cursor
            val projection = arrayOf<String>()
            val cursor = SQuery.newQuery()
                .expr(TrackstageBrother.REST_ID, SQuery.Op.EQ, 0)
                .or()
                .append(TrackstageBrother.REST_ID + " is null")
                .select(TrackstageBrother.CONTENT_URI, projection)
            var zlr = 0
            while (cursor.moveToNext()) {
                zlr++
                var valueChanged = false
                LoggingHelperAdmin.setMessage(zlr.toString() + "/" + cursor.count + " push")
                val stageBrother = TrackstageBrotherRecord.fromCursor(cursor)
                val track = getTracksRecord(stageBrother) // get track or empty dummy
                val restTrackstage = RESTtrackStage()
                restTrackstage.androidid =
                    if (stageBrother.androidid == "debug") "confirm" else "check"
                if (stageBrother.trackname != null) {
                    if (track.trackname != stageBrother.trackname) {
                        restTrackstage.trackname = stageBrother.trackname
                        valueChanged = true
                    }
                }
                if (stageBrother.latitude != 0.0) {
                    restTrackstage.latitude = stageBrother.latitude
                    valueChanged = true
                }
                if (stageBrother.longitude != 0.0) {
                    restTrackstage.longitude = stageBrother.longitude
                    valueChanged = true
                }
                if (track.country != stageBrother.country) {
                    restTrackstage.country = stageBrother.country
                    valueChanged = true
                }
                // restTrackstage.setChanged((int) stageBrother.getCreated());
                restTrackstage.changed = System.currentTimeMillis() / 1000
                // einen gibt es noch, wo in der Tel eine eMail steht
                if (stageBrother.phone != null) {
                    if (stageBrother.phone.contains("@")) {
                        if (SecHelper.decryptB64(track.contact) != stageBrother.phone) {
                            restTrackstage.contact = stageBrother.phone
                            valueChanged = true
                        }
                    } else {
                        val phone = stageBrother.phone.replace("\\(0\\)".toRegex(), "")
                        if (SecHelper.decryptB64(track.phone) != phone) {
                            restTrackstage.phone = phone
                            valueChanged = true
                        }
                    }
                }
                if (SecHelper.decryptB64(track.url) != stageBrother.url) {
                    restTrackstage.url = stageBrother.url
                    valueChanged = true
                }
                if (track.notes != stageBrother.notes) {
                    restTrackstage.notes = stageBrother.notes
                    valueChanged = true
                }
                if (track.openmondays != stageBrother.openmondays && stageBrother.openmondays != 0L) {
                    restTrackstage.openmondays = stageBrother.openmondays.toInt()
                    valueChanged = true
                }
                if (track.opentuesdays != stageBrother.opentuesdays && stageBrother.opentuesdays != 0L) {
                    restTrackstage.opentuesdays = stageBrother.opentuesdays.toInt()
                    valueChanged = true
                }
                if (track.openwednesday != stageBrother.openwednesday && stageBrother.openwednesday != 0L) {
                    restTrackstage.openwednesday = stageBrother.openwednesday.toInt()
                    valueChanged = true
                }
                if (track.openthursday != stageBrother.openthursday && stageBrother.openthursday != 0L) {
                    restTrackstage.openthursday = stageBrother.openthursday.toInt()
                    valueChanged = true
                }
                if (track.openfriday != stageBrother.openfriday && stageBrother.openfriday != 0L) {
                    restTrackstage.openfriday = stageBrother.openfriday.toInt()
                    valueChanged = true
                }
                if (track.opensaturday != stageBrother.opensaturday && stageBrother.opensaturday != 0L) {
                    restTrackstage.opensaturday = stageBrother.opensaturday.toInt()
                    valueChanged = true
                }
                if (track.opensunday != stageBrother.opensunday && stageBrother.opensunday != 0L) {
                    restTrackstage.opensunday = stageBrother.opensunday.toInt()
                    valueChanged = true
                }
                if (track.hoursmonday != stageBrother.hoursmonday.trim { it <= ' ' }) {
                    restTrackstage.hoursmonday = stageBrother.hoursmonday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hourstuesday != stageBrother.hourstuesday) {
                    restTrackstage.hourstuesday = stageBrother.hourstuesday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hourswednesday != stageBrother.hourswednesday) {
                    restTrackstage.hourswednesday = stageBrother.hourswednesday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hoursthursday != stageBrother.hoursthursday) {
                    restTrackstage.hoursthursday = stageBrother.hoursthursday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hoursfriday != stageBrother.hoursfriday) {
                    restTrackstage.hoursfriday = stageBrother.hoursfriday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hourssaturday != stageBrother.hourssaturday) {
                    restTrackstage.hourssaturday = stageBrother.hourssaturday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.hourssunday != stageBrother.hourssunday) {
                    restTrackstage.hourssunday = stageBrother.hourssunday.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.trackaccess != stageBrother.trackaccess) {
                    restTrackstage.trackaccess = stageBrother.trackaccess.trim { it <= ' ' }
                    valueChanged = true
                }
                if (track.mxtrack == 0L) {
                    restTrackstage.mxtrack = 1
                    valueChanged = true
                }
                if (stageBrother.trackRestId > 0) {
                    restTrackstage.trackId = stageBrother.trackRestId.toInt()
                    restTrackstage.androidid = "debug"
                    if (valueChanged) {
                        restTrackstage.androidid = "confirm"
                    }
                } else { // neuanlage
                    restTrackstage.androidid = "debug"
                }
                val request = PostTrackstageIDRequest(restTrackstage)
                val res = webClient.postTrackstageID(request)
                // res.checkResponseCode(204);
                res.checkResponseCodeOk()
                val resTrackStage = res.parse()
                if (resTrackStage.insertResponse.trackRestId > 0) {
                    val valuesVid = ContentValues()
                    valuesVid.put(Videos.TRACK_REST_ID, resTrackStage.insertResponse.trackRestId)
                    val vidUpd =
                        SQuery.newQuery().expr(Videos.TRACK_ID, SQuery.Op.EQ, stageBrother.id)
                            .update(Videos.CONTENT_URI, valuesVid)
                    val values = ContentValues()
                    values.put(PictureStage.TRACK_REST_ID, resTrackStage.insertResponse.trackRestId)
                    val imgUpd =
                        SQuery.newQuery().expr(PictureStage.TRACK_ID, SQuery.Op.EQ, stageBrother.id)
                            .update(PictureStage.CONTENT_URI, values)
                    Timber.d("img upd:$imgUpd vid:$vidUpd")
                }
                stageBrother.restId = resTrackStage.insertResponse.id.toLong()
                stageBrother.trackRestId = resTrackStage.insertResponse.trackRestId.toLong()
                stageBrother.save(false)
                delay()
            }
            doDownloadImages(commApiClient)
            doDownloadVideos(commApiClient)
            doPushImages()
            LoggingHelperAdmin.setMessage("")
        } catch (e: Exception) {
            Timber.e(e)
            return OperationResult.error(e)
        }
        return OperationResult.ok(bundle)
    }

    private fun getTracksRecord(stageBrother: TrackstageBrotherRecord): TracksRecord {
        var track = SQuery.newQuery()
            .expr(Tracks.REST_ID, SQuery.Op.EQ, stageBrother.trackRestId)
            .selectFirst<TracksRecord>(Tracks.CONTENT_URI)
        if (track == null) {
            track =
                TracksRecord() // dummy, wird nicht gespeichert, mach nur die abfrage auf null einfacher
            track.trackname = ""
            track.country = ""
            track.contact = ""
            track.phone = ""
            track.url = ""
            track.notes = ""
            track.trackaccess = ""
            track.hoursmonday = ""
            track.hourstuesday = ""
            track.hourswednesday = ""
            track.hoursthursday = ""
            track.hoursfriday = ""
            track.hourssaturday = ""
            track.hourssunday = ""
        } else {
            if (track.country == null) {
                track.country = ""
            }
            if (track.contact == null) {
                track.contact = ""
            }
            if (track.phone == null) {
                track.phone = ""
            }
            if (track.url == null) {
                track.url = ""
            }
            if (track.notes == null) {
                track.notes = ""
            }
            if (track.trackaccess == null) {
                track.trackaccess = ""
            }
            if (track.hoursmonday == null) {
                track.hoursmonday = ""
            }
            if (track.hourstuesday == null) {
                track.hourstuesday = ""
            }
            if (track.hourswednesday == null) {
                track.hourswednesday = ""
            }
            if (track.hoursthursday == null) {
                track.hoursthursday = ""
            }
            if (track.hoursfriday == null) {
                track.hoursfriday = ""
            }
            if (track.hourssaturday == null) {
                track.hourssaturday = ""
            }
            if (track.hourssunday == null) {
                track.hourssunday = ""
            }
        }
        return track
    }

    private fun doPushImages() {
        val withValidTrack = SQuery.newQuery().expr(PictureStage.TRACK_REST_ID, SQuery.Op.NEQ, 0)
            .or()
            .append(PictureStage.TRACK_REST_ID + " is not null")
        val notTransferred = SQuery.newQuery().expr(PictureStage.REST_ID, SQuery.Op.NEQ, 0)
            .or()
            .append(PictureStage.REST_ID + " is not null")
        val withLocalFile = SQuery.newQuery().expr(PictureStage.LOCAL_FILE, SQuery.Op.NEQ, "")
            .or()
            .append(PictureStage.LOCAL_FILE + " is not null")
        val projection = arrayOf<String>()
        val cursor = SQuery.newQuery()
            .expr(notTransferred)
            .expr(withLocalFile)
            .expr(withValidTrack)
            .select(PictureStage.CONTENT_URI, projection)
        var zlr = 0
        while (cursor.moveToNext()) {
            zlr++
            LoggingHelperAdmin.setMessage("$zlr download from www")
            val picRecord = PictureStageRecord.fromCursor(cursor)
            val recordFile = File(picRecord.localFile)
            if (recordFile.exists()) {
                val response = doPostImage(picRecord)
                if (response!!.id > 0) {
                    picRecord.restId = response.id.toLong()
                    picRecord.changed = response.changed - 1 // force update from server
                    picRecord.save(false)
                }
            }
        }
    }

    private fun doPostImage(picRecord: PictureStageRecord): InsertResponse? {
        val basic = aadhresUBase
        val file = File(picRecord.localFile)
        val fileReqBody: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, fileReqBody)

        val call = commApiClient.pictureService.postPicture(
            filePart,
            "",
            picRecord.trackRestId,
            "",
            "debug", basic
        )
        try {
            val result = call.execute()
            return result.body()
        } catch (e: IOException) {
            Timber.e(e)
        }
        return null
    }

    companion object {

        @Throws(IOException::class)
        fun doDownloadImages(apiClient: CommApiClient) {
            val notTransferred = SQuery.newQuery().expr(PictureStage.REST_ID, SQuery.Op.EQ, 0)
                .or()
                .append(PictureStage.REST_ID + " is null")
            val noLocalFile = SQuery.newQuery().expr(PictureStage.LOCAL_FILE, SQuery.Op.EQ, "")
                .or()
                .append(PictureStage.LOCAL_FILE + " is null")
            val projection = arrayOf<String>()
            val cursor = SQuery.newQuery()
                .expr(notTransferred)
                .expr(noLocalFile)
                .select(PictureStage.CONTENT_URI, projection)
            var zlr = 0
            while (cursor.moveToNext()) {
                zlr++
                LoggingHelperAdmin.setMessage(zlr.toString() + "/" + cursor.count + " Pic download from www")
                val picture = PictureStageRecord.fromCursor(cursor)
                if (!picture.www.endsWith("no_image_available.gif")) {
                    val file = File(
                        getExtFilesDir(true) +
                                File.separatorChar + "id" + picture.id + ".jpg"
                    )
                    if (RetroFileHelper.downloadFile(apiClient, picture.www, file.absolutePath)) {
                        picture.localFile = file.absolutePath
                        picture.save(false)
                    }
                }
            }
        }

        @Throws(IOException::class)
        fun doDownloadVideos(apiClient: CommApiClient) {
            val notTransferred = SQuery.newQuery().expr(Videos.REST_ID, SQuery.Op.EQ, 0)
                .or()
                .append(Videos.REST_ID + " is null")
            val noLocalFile = SQuery.newQuery().expr(Videos.LOCAL_FILE, SQuery.Op.EQ, "")
                .or()
                .append(Videos.LOCAL_FILE + " is null")
            val projection = arrayOf<String>()
            val cursor = SQuery.newQuery()
                .expr(notTransferred)
                .expr(noLocalFile)
                .select(Videos.CONTENT_URI, projection)
            var zlr = 0
            while (cursor.moveToNext()) {
                zlr++
                LoggingHelperAdmin.setMessage(zlr.toString() + "/" + cursor.count + " Video download from www")
                val video = VideosRecord.fromCursor(cursor)
                val file = File(
                    getExtFilesDir(true) +
                            File.separatorChar + "id" + video.id + ".wmv"
                )
                if (RetroFileHelper.downloadFile(apiClient, video.www, file.absolutePath)) {
                    video.localFile = file.absolutePath
                    video.save(false)
                }
            }
        }

        fun getExtFilesDir(any: Boolean): String {
            val externalLocations = ExternalStorage.allStorageLocations
            // final File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
            val externalSdCard = externalLocations[ExternalStorage.EXTERNAL_SD_CARD]
            var pathSD = if (externalSdCard == null) "" else externalSdCard.absolutePath
            if (pathSD == "" && any) {
                pathSD = ExternalStorage.sdCardPath
            }
            return pathSD
        }
    }
}
