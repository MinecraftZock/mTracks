package info.hannes.mxadmin.download

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.OperationExecutor
import com.robotoworks.mechanoid.ops.OperationExecutorCallbacks
import com.robotoworks.mechanoid.ops.OperationResult
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.utils.ViewUtils.setTotalHeightOfListView
import info.hannes.mechadminGen.ops.mxcal.AbstractOpBrothersLoadOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpBrothersPushOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpLoadPictureVideoOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpMxCalLoadSearchOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpMxCallFixLatLngOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpMxCallPush2ServerOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpRiderPlanetLoadOperation
import info.hannes.mechadminGen.ops.mxcal.AbstractOpTracksMapLoadOperation
import info.hannes.mechadminGen.ops.mxcal.OpRiderPlanetLoadOperation
import info.hannes.mechadminGen.sqlite.DownLoadSiteRecord
import info.hannes.mechadminGen.sqlite.MxAdminDBContract
import info.hannes.mechadminGen.sqlite.MxAdminDBContract.TrackstageBrother
import info.hannes.mechadminGen.sqlite.MxAdminDBContract.Videos
import info.hannes.mechadminGen.sqlite.MxCalContract.QuellFile
import info.hannes.mechadminGen.sqlite.QuellFileRecord
import info.mx.tracks.MxAccessApplication.Companion.aadhresUBase
import info.mx.tracks.R
import info.mx.tracks.common.ImportStatusMessage
import info.mx.tracks.ops.OpSyncFromServerOperation
import timber.log.Timber

/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
 */
class FragmentDownloadDetail : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var downLoadType: DownLoadType? = null
    private var serverUrl: String = ""
    private var adapter: SimpleCursorAdapter? = null
    private val projection =
        arrayOf(QuellFile.UPDATED_COUNT, QuellFile.URL, QuellFile.CREATEDATE, QuellFile.CONTENT)
    private val to = intArrayOf(R.id.txUpdate, R.id.txUrl, R.id.txCreatedate, R.id.webView)
    private var bAll = false
    private var tvProgress: TextView? = null
    private var lyProgress: View? = null
    private var serverUrlGes: String? = null
    private var downLoadId: Long = 0
    private var moviesOperationExecutor: OperationExecutor? = null
    private var adapterSteps: StepsBrotherAdapter? = null

    internal enum class DownLoadType {
        MX_CAL, RIDER_PLANET, TRACKS_MAP, MX_BROTHER
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            val mId = requireArguments().getLong(ARG_ITEM_ID)
            val rec = DownLoadSiteRecord.get(mId)
            if (rec != null) {
                serverUrlGes = rec.url
                serverUrl = rec.url
                downLoadId = rec.id
                serverUrl = serverUrl.substring(0, serverUrl.lastIndexOf("/"))
                if (serverUrl.contains("tracksmap.com")) {
                    downLoadType = DownLoadType.TRACKS_MAP
                } else if (serverUrl.contains("riderplanet-usa.com")) {
                    downLoadType = DownLoadType.RIDER_PLANET
                } else if (serverUrl.contains("mxcal.com")) {
                    downLoadType = DownLoadType.MX_CAL
                } else if (serverUrl.contains("mxbrothers.com")) {
                    downLoadType = DownLoadType.MX_BROTHER
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "$serverUrl not implemented",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(requireActivity(), "$mId not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View
        val importListView: ListView
        if (downLoadType == DownLoadType.MX_BROTHER) {
            rootView = inflater.inflate(R.layout.screen_list_with_progressbar_steps, null)
            val importSteps = rootView.findViewById<ListView>(R.id.listSteps)
            adapterSteps = StepsBrotherAdapter()
            importSteps.adapter = adapterSteps
            setTotalHeightOfListView(importSteps)
            importListView = rootView.findViewById(R.id.list_xyz)
        } else {
            rootView = inflater.inflate(R.layout.screen_list_with_progressbar, null)
            importListView = rootView.findViewById(R.id.list_overview)
        }
        importListView.onItemLongClickListener =
            OnItemLongClickListener { _: AdapterView<*>?, _: View?, _: Int, id: Long ->
                val rec = QuellFileRecord.get(id)
                if (rec != null) {
                    rec.updatedCount = -1
                    rec.save()
                } else {
                    Toast.makeText(requireActivity(), "$id not found", Toast.LENGTH_LONG).show()
                }
                true
            }
        adapter =
            SimpleCursorAdapter(requireActivity(), R.layout.item_quellfile, null, projection, to, 0)
        importListView.adapter = adapter
        adapter!!.viewBinder = ViewBinderQuellFile()

        // ImportProgress issue
        tvProgress = rootView.findViewById(R.id.textProgress)
        tvProgress?.text = ""
        lyProgress = rootView.findViewById(R.id.lyImportProgress)
        lyProgress?.visibility = View.GONE
        return rootView
    }

    private fun handleMenuItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_reset1) {
            val rowsAffected = QuellFile.newBuilder()
                .setUpdatedCount(-1)
                .update(SQuery.newQuery().append(QuellFile.CONTENT + " <> ''"))
            Toast.makeText(requireActivity(), "Reset1: $rowsAffected", Toast.LENGTH_SHORT).show()
        } else if (i == R.id.menu_download_pictures) {
            doDownLoadPicturesVideos()
        } else if (i == R.id.menu_delete) {
            doDelete()
        } else if (i == R.id.menu_getgeo) {
            fixMissingLatLng()
        } else if (i == R.id.menu_push2server) {
            doUpload()
        } else if (i == R.id.menu_download) {
            doDownload()
        } else if (i == R.id.menu_alle) {
            bAll = !bAll
            if (bAll) {
                item.setIcon(R.drawable.actionbar_checkbox)
                item.title = "Alle"
            } else {
                item.setIcon(R.drawable.actionbar_checkbox_empty)
                item.title = "nur aktuelle"
            }
        }
        return true
    }

    private fun doDownLoadPicturesVideos() {
        val intent: Intent = AbstractOpLoadPictureVideoOperation.newIntent()
        Ops.execute(intent)
    }

    private fun fixMissingLatLng() {
        val intent: Intent = AbstractOpMxCallFixLatLngOperation.newIntent(bAll)
        Ops.execute(intent)
    }

    private fun doDelete() {
        if (downLoadType == DownLoadType.MX_BROTHER) {
            val countTransferred = SQuery.newQuery()
                .expr(TrackstageBrother.REST_ID, SQuery.Op.GT, 0)
                .count(TrackstageBrother.CONTENT_URI)
            val countWithXMLContent = SQuery.newQuery()
                .expr(TrackstageBrother.CONTENT_DETAIL_XML, SQuery.Op.NEQ, "")
                .count(TrackstageBrother.CONTENT_URI)
            if (countTransferred > 0) {
                val values = TrackstageBrother.newBuilder()
                    .setRestId(0)
                    .values
                val reseted = SQuery.newQuery()
                    .expr(TrackstageBrother.REST_ID, SQuery.Op.GT, 0)
                    .update(TrackstageBrother.CONTENT_URI, values)
                val valuesImg = MxAdminDBContract.PictureStage.newBuilder()
                    .setRestId(0)
                    .values
                SQuery.newQuery()
                    .expr(MxAdminDBContract.PictureStage.REST_ID, SQuery.Op.GT, 0)
                    .update(MxAdminDBContract.PictureStage.CONTENT_URI, valuesImg)
                val valuesVid = Videos.newBuilder()
                    .setRestId(0)
                    .values
                SQuery.newQuery()
                    .expr(Videos.REST_ID, SQuery.Op.GT, 0)
                    .update(Videos.CONTENT_URI, valuesVid)
                Timber.d("TrackstageBrother transfer reset:%s", reseted)
                Toast.makeText(requireActivity(), "$reseted reset", Toast.LENGTH_LONG).show()
            } else if (countWithXMLContent > 0) {
                val values = TrackstageBrother.newBuilder()
                    .setContentDetailXml("")
                    .values
                val reseted = SQuery.newQuery()
                    .expr(TrackstageBrother.CONTENT_DETAIL_XML, SQuery.Op.NEQ, "")
                    .update(TrackstageBrother.CONTENT_URI, values)
                Timber.d("TrackstageBrother html reset:%s", reseted)
                Toast.makeText(requireActivity(), "$reseted reset", Toast.LENGTH_LONG).show()
            } else {
                val deleted = TrackstageBrother.delete()
                Timber.d("TrackstageBrother deleted:%s", deleted)
                Toast.makeText(requireActivity(), "$deleted deleted", Toast.LENGTH_LONG).show()
            }
        } else {
            val qu = SQuery.newQuery()
                .expr(QuellFile.CONTENT, SQuery.Op.EQ, "")
                .or()
                .expr(QuellFile.UPDATED_COUNT, SQuery.Op.EQ, 0)
                .or()
                .append(QuellFile.UPDATED_COUNT + " is null")
                .or()
                .append("1=" + if (bAll) 1 else 0)
            val anz = SQuery.newQuery()
                .expr(QuellFile.URL, SQuery.Op.LIKE, "$serverUrl%")
                .expr(qu)
                .delete(QuellFile.CONTENT_URI)
            Toast.makeText(requireActivity(), "$anz deleted", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (moviesOperationExecutor != null) {
            moviesOperationExecutor!!.saveState(outState)
        }
    }

    private fun doUpload() {
        if (downLoadType == DownLoadType.MX_BROTHER) {
            val intent: Intent = AbstractOpBrothersPushOperation.newIntent()
            Ops.execute(intent)
        } else if (downLoadType == DownLoadType.TRACKS_MAP) {
            val intent: Intent = AbstractOpTracksMapLoadOperation.newIntent(serverUrlGes, downLoadId, true)
            Ops.execute(intent)
        } else if (downLoadType == DownLoadType.RIDER_PLANET) {
            moviesOperationExecutor = OperationExecutor(OP_GET_RIDER, null, mOpExecCallbacks)
            moviesOperationExecutor!!.execute(
                AbstractOpRiderPlanetLoadOperation.newIntent(
                    serverUrlGes,
                    downLoadId,
                    true
                ), OperationExecutor.MODE_ONCE
            )
        } else if (downLoadType == DownLoadType.MX_CAL) {
            val intent: Intent = AbstractOpMxCallPush2ServerOperation.newIntent(aadhresUBase)
            Ops.execute(intent)
        } else {
            Toast.makeText(requireActivity(), "$serverUrl not implemented", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun doDownload() {
        if (downLoadType == DownLoadType.TRACKS_MAP) {
            val intent: Intent =
                AbstractOpTracksMapLoadOperation.newIntent(serverUrlGes, downLoadId, false)
            Ops.execute(intent)
        } else if (downLoadType == DownLoadType.RIDER_PLANET) {
            val intent: Intent =
                AbstractOpRiderPlanetLoadOperation.newIntent(serverUrlGes, downLoadId, false)
            Ops.execute(intent)
        } else if (downLoadType == DownLoadType.MX_CAL) {
            val intent: Intent = AbstractOpMxCalLoadSearchOperation.newIntent(bAll)
            Ops.execute(intent)
        } else if (downLoadType == DownLoadType.MX_BROTHER) {
            val intent: Intent = AbstractOpBrothersLoadOperation.newIntent(serverUrlGes, downLoadId, true)
            Ops.execute(intent)
        } else {
            Toast.makeText(requireActivity(), "$serverUrl not implemented", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup menu using MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_download_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        loaderManager.initLoader(LOADER_QUELL_FILE, this.arguments, this)
        loaderManager.initLoader(LOADER_BROTHER_TRACKS, arguments, this)

        lyProgress!!.visibility = View.GONE
        OpSyncFromServerOperation.adminImportStatusCalMessage.observe(viewLifecycleOwner) { msg ->
            onImportStatusMessage(msg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OpSyncFromServerOperation.adminImportStatusCalMessage.removeObservers(this)
    }

    private fun onImportStatusMessage(importStatusMessage: ImportStatusMessage) {
        if (importStatusMessage.message.isNotBlank()) {
            lyProgress!!.visibility = View.VISIBLE
        } else {
            lyProgress!!.visibility = View.GONE
        }
        tvProgress!!.text = importStatusMessage.message
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        return when (loader) {
            LOADER_BROTHER_TRACKS -> SQuery.newQuery()
                .createSupportLoader(TrackstageBrother.CONTENT_URI, null, null)

            LOADER_QUELL_FILE -> {
                val query = SQuery.newQuery()
                    .expr(QuellFile.URL, SQuery.Op.LIKE, "$serverUrl%")
                query.createSupportLoader(
                    QuellFile.CONTENT_URI,
                    null,
                    QuellFile.CREATEDATE + " desc"
                )
            }

            else -> throw RuntimeException("ImportstatusCal removed")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_BROTHER_TRACKS -> if (adapterSteps != null) {
                adapterSteps!!.notifyDataSetChanged()
            }

            LOADER_QUELL_FILE -> adapter!!.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_QUELL_FILE -> {
                adapter!!.swapCursor(null)
                lyProgress!!.visibility = View.GONE
            }
        }
    }

    private fun showDlg(context: Context, text: String?) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder
            .setMessage(text)
            .setCancelable(true)
            .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private val mOpExecCallbacks: OperationExecutorCallbacks = object : OperationExecutorCallbacks {
        override fun onOperationComplete(key: String, result: OperationResult): Boolean {
            if (OP_GET_RIDER == key) {
                if (result.isOk) {
                    if (result.data.containsKey(
                            OpRiderPlanetLoadOperation.RESULT_STR
                        )
                    ) {
                        showDlg(
                            requireActivity(),
                            result.data.getString(
                                OpRiderPlanetLoadOperation.RESULT_STR
                            )
                        )
                    }
                } else {
                    Toast.makeText(requireActivity(), result.error.message, Toast.LENGTH_LONG)
                        .show()
                }
                return true
            }
            return false
        }

        override fun onOperationPending(key: String) {}
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
        private const val LOADER_QUELL_FILE = 0
        private const val LOADER_BROTHER_TRACKS = 2
        private const val OP_GET_RIDER = "OP_GET_RIDER"
    }
}
