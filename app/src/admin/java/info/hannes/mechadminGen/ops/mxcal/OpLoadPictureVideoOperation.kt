package info.hannes.mechadminGen.ops.mxcal

import android.os.Bundle
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationContext
import com.robotoworks.mechanoid.ops.OperationResult
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadminGen.ops.mxcal.OpBrothersPushOperation.Companion.doDownloadImages
import info.hannes.mechadminGen.ops.mxcal.OpBrothersPushOperation.Companion.doDownloadVideos
import info.hannes.mechadminGen.sqlite.MxAdminDBContract
import info.hannes.mechadminGen.sqlite.PictureStageRecord
import info.hannes.mechadminGen.sqlite.TrackstageBrotherRecord
import info.hannes.mechadminGen.sqlite.VideosRecord
import info.mx.comlib.retrofit.CommApiClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

internal class OpLoadPictureVideoOperation : AbstractOpLoadPictureVideoOperation(), KoinComponent {

    val apiClient: CommApiClient by inject()

    override fun onExecute(context: OperationContext, args: Args): OperationResult {
        val bundle = Bundle()
        try {
            fixMissingTrackRestID()
            doDownloadImages(apiClient)
            doDownloadVideos(apiClient)
            LoggingHelperAdmin.setMessage("")
        } catch (e: Exception) {
            Timber.e(e.message)
            return OperationResult.error(e)
        }
        return OperationResult.ok(bundle)
    }

    companion object {
        /**
         * fix missing TrackRestID
         */
        fun fixMissingTrackRestID() {
            val pictures = SQuery.newQuery()
                .expr(MxAdminDBContract.PictureStage.TRACK_REST_ID, SQuery.Op.EQ, 0)
                .select<PictureStageRecord>(MxAdminDBContract.PictureStage.CONTENT_URI)
            var clr = 0
            for (picture in pictures) {
                val track = TrackstageBrotherRecord.get(picture.trackId)
                if (track != null) {
                    picture.trackRestId = track.trackRestId
                    picture.save(false)
                    clr++
                }
            }
            Timber.d("PictureStageRecord set trackRestID:$clr")

            //            // fix empty pics
            //            pictures = SQuery.newQuery()
            //                    .expr(MxAdminDBContract.PictureStage.WWW, SQuery.Op.LIKE, "%no_image_available.gif")
            //                    .select(MxAdminDBContract.PictureStage.CONTENT_URI);
            //            clr = 0;
            //            for (PictureStageRecord picture : pictures) {
            //                File toDelete = new File(picture.getLocalFile());
            //                LoggingHelperAdmin.setMessage("delete "+clr);
            //                if (toDelete.exists()) {
            //                    toDelete.delete();
            //                    picture.delete(false);
            //                }
            //                clr++;
            //            }
            //            SQuery.newQuery()
            //                    .expr(MxAdminDBContract.PictureStage.WWW, SQuery.Op.LIKE, "%no_image_available.gif")
            //                    .delete(MxAdminDBContract.PictureStage.CONTENT_URI);
            val videos = SQuery.newQuery()
                .expr(MxAdminDBContract.Videos.TRACK_REST_ID, SQuery.Op.EQ, 0)
                .select<VideosRecord>(MxAdminDBContract.Videos.CONTENT_URI)
            clr = 0
            for (video in videos) {
                val track = TrackstageBrotherRecord.get(video.trackId)
                if (track != null) {
                    video.trackRestId = track.trackRestId
                    video.save(false)
                    clr++
                }
            }
            Timber.d("VideosRecord set trackRestID:$clr")
        }
    }
}
