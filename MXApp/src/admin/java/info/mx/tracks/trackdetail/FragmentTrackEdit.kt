package info.mx.tracks.trackdetail

import android.database.Cursor
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.tracks.MxApplication.Companion.isGoogleTests
import info.mx.tracks.R
import info.mx.tracks.common.StageHelper
import info.mx.tracks.react.ErrorCommand
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage
import info.mx.tracks.sqlite.TrackstageRecord
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import retrofit2.Response
import timber.log.Timber

internal class FragmentTrackEdit : BaseFragmentTrackEdit(), LoaderManager.LoaderCallbacks<Cursor> {

    private val dataManagerAdmin: DataManagerAdmin by inject()
    override fun fillMask(newId: Long) {
        super.fillMask(newId)
        super.binding.teApproved.visibility =
            if (!isGoogleTests && recordTrack!!.restId > 0)
                View.VISIBLE
            else
                View.GONE
    }

    override fun doAfterMapLoaded() {
        if (!isGoogleTests)
            loaderManager.initLoader(LOADER_STAGE, null, this@FragmentTrackEdit)
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        return StageHelper.getStageQuery(recordTrack!!.restId)
            .expr(Trackstage.TRACK_REST_ID, SQuery.Op.NEQ, 0)
            .createSupportLoader(Trackstage.CONTENT_URI, null, Trackstage.CREATED)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_STAGE -> if (cursor.count > 0) {
                StageHelper.addStageMarkers(map, cursor, true)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_STAGE -> {
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, view, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_trackstage_del, menu)
        menu.setHeaderTitle("What to do")
        stageId = view.tag as Long
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (isGoogleTests) return super.onContextItemSelected(item)
        return if (item.itemId == R.id.trackstage_decline) {
            addDisposable(
                dataManagerAdmin.trackStageDecline(stageId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { Timber.d("()") }
                    .subscribe({
                        val stage =
                            SQuery.newQuery().expr(Trackstage.REST_ID, SQuery.Op.EQ, stageId!!)
                                .selectFirst<TrackstageRecord>(Trackstage.CONTENT_URI)
                        stage.approved = -1
                        stage.save(true)
                    }
                    ) { ErrorCommand(requireContext()) }
            )
            true
        } else if (item.itemId == R.id.trackstage_ignore) {
            addDisposable(
                dataManagerAdmin.trackStageDelete(stageId!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { Timber.d("()") }
                    .subscribe({ result: Response<Void?> ->
                        if (result.code() == com.robotoworks.mechanoid.net.Response.HTTP_NO_CONTENT) {
                            val stage =
                                SQuery.newQuery().expr(Trackstage.REST_ID, SQuery.Op.EQ, stageId!!)
                                    .selectFirst<TrackstageRecord>(Trackstage.CONTENT_URI)
                            stage.delete()
                        }
                    }
                    ) { e: Throwable? -> Timber.e(e) }
            )
            true
        } else {
            super.onContextItemSelected(item)
        }
    }

    companion object {
        private const val LOADER_STAGE = 0

        @JvmField
        var projectionStage = arrayOf(
            Trackstage._ID, Trackstage.ANDROIDID,
            Trackstage.ANDROIDID, Trackstage.ANDROIDID, Trackstage.INS_DISTANCE,
            Trackstage.REST_ID, Trackstage.REST_ID, Trackstage.ANDROIDID
        )

        @JvmField
        val toStage = intArrayOf(
            R.id.textLeft, R.id.textOpenVal,
            R.id.textAcceptVal, R.id.textDeclineVal, R.id.textDist,
            R.id.btnAccept, R.id.btnDecline, R.id.textUser
        )
    }
}
