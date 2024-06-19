package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.ListView
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.comlib.retrofit.service.data.BaseSingleObserver
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxApplication.Companion.isGoogleTests
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.StageHelper
import info.mx.tracks.ops.AbstractOpSyncFromServerOperation
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.sqlite.TrackstageRecord
import info.mx.tracks.stage.StageViewBinder
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.util.*

class FragmentStage : FragmentUpDown(), LoaderManager.LoaderCallbacks<Cursor> {
    private var adapterStage: SimpleCursorAdapter? = null
    private var stageId: Long? = null

    private val dataManagerAdmin: DataManagerAdmin by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.fragment_list, null)
        val emptyView = view.findViewById<TextView>(R.id.txt_no_entry)
        val listRatings = view.findViewById<ListView>(R.id.listEntries)
        listRatings.emptyView = emptyView
        adapterStage = SimpleCursorAdapter(
            requireContext(), R.layout.item_stage, null,
            FragmentTrackEdit.projectionStage, FragmentTrackEdit.toStage, 0
        )
        adapterStage!!.viewBinder = StageViewBinder(this, requireActivity(), dataManagerAdmin)
        listRatings.adapter = adapterStage
        recordLocalId = 0
        if (arguments != null && requireArguments().containsKey(RECORD_ID_LOCAL)) {
            recordLocalId = requireArguments().getLong(RECORD_ID_LOCAL)
        }
        fillMask(recordLocalId)
        if (!isGoogleTests) loaderManager.initLoader(LOADER_STAGE, arguments, this)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, view, menuInfo)
        Objects.requireNonNull(requireActivity()).menuInflater.inflate(
            R.menu.context_trackstage_del,
            menu
        )
        menu.setHeaderTitle("What to do")
        stageId = view.tag as Long
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (isGoogleTests) return super.onContextItemSelected(item)

        // TODO stageId should be a part of the response
        return if (item.itemId == R.id.trackstage_decline) {
            dataManagerAdmin.trackStageDecline(stageId!!)
                .subscribe(object : BaseSingleObserver<String?>(requireContext()) {
                    override fun onSuccess(result: String) {
                        val stage =
                            SQuery.newQuery().expr(Trackstage.REST_ID, SQuery.Op.EQ, stageId!!)
                                .selectFirst<TrackstageRecord>(Trackstage.CONTENT_URI)
                        stage.approved = -1
                        stage.save(true)
                        val intentM: Intent =
                            AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                        Ops.execute(intentM)
                    }
                })
            true
        } else if (item.itemId == R.id.trackstage_ignore) {
            dataManagerAdmin.trackStageDelete(stageId!!)
                .subscribe(object : BaseSingleObserver<Response<Void?>?>(requireContext()) {
                    override fun onSuccess(result: Response<Void?>) {
                        if (result.code() == com.robotoworks.mechanoid.net.Response.HTTP_NO_CONTENT) {
                            val stage =
                                SQuery.newQuery().expr(Trackstage.REST_ID, SQuery.Op.EQ, stageId!!)
                                    .selectFirst<TrackstageRecord>(Trackstage.CONTENT_URI)
                            stage.delete()
                            val intentM: Intent =
                                AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                            Ops.execute(intentM)
                        }
                    }
                })
            true
        } else {
            super.onContextItemSelected(item)
        }
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        var supportLoader: CursorLoader? = null
        val recordTrack = TracksRecord.get(bundle!!.getLong(RECORD_ID_LOCAL))

        when (loader) {
            LOADER_STAGE -> supportLoader = StageHelper.getStageQuery(recordTrack.restId)
                .expr(Trackstage.TRACK_REST_ID, SQuery.Op.NEQ, 0)
                .createSupportLoader(Trackstage.CONTENT_URI, null, Trackstage.CREATED)
        }
        return supportLoader!!
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_STAGE -> adapterStage!!.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_STAGE -> adapterStage!!.swapCursor(null)
        }
    }

    override fun fillMask(localId: Long) {
        if (arguments != null) {
            requireArguments().putLong(RECORD_ID_LOCAL, localId)
            loaderManager.restartLoader(LOADER_STAGE, arguments, this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_track_detail, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_detail_globus).isVisible = false
        menu.findItem(R.id.menu_detail_radar).isVisible = false
        menu.findItem(R.id.menu_favorit).isVisible = false
        menu.findItem(R.id.menu_detail_share).isVisible = false
        menu.findItem(R.id.menu_detail_radar).isVisible = false
        menu.findItem(R.id.menu_navigation).isVisible = false
        menu.findItem(R.id.menu_event_add).isVisible = false
        menu.findItem(R.id.menu_track_edit)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        menu.findItem(R.id.menu_navigation)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var res = super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menu_track_edit) {
            openEdit(recordLocalId)
            res = true
        }
        return res
    }

    private fun openEdit(id: Long) {
        val qWfIntent = Intent(requireActivity(), ActivityTrackEdit::class.java)
        val bundle = Bundle()
        bundle.putLong(RECORD_ID_LOCAL, id)
        qWfIntent.putExtras(bundle)
        startActivity(qWfIntent)
    }

    companion object {
        private const val LOADER_STAGE = 0
    }
}