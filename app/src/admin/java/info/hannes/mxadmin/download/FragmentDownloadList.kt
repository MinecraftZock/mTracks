package info.hannes.mxadmin.download

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.TrackingApplication
import info.hannes.commonlib.utils.BackupHelper
import info.hannes.mechadmin.LoggingHelperAdmin
import info.hannes.mechadminGen.ops.OpFacebookSplitOperation
import info.hannes.mechadminGen.ops.OpStageSplitOperation
import info.hannes.mechadminGen.sqlite.MxCalContract
import info.hannes.mxadmin.picture.ActivityImageConfirm
import info.hannes.mxadmin.picture.ActivityImageMXBrotherStage
import info.hannes.mxadmin.service.DataManagerAdmin
import info.hannes.retrofit.service.model.VersionInfo
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.comlib.retrofit.service.data.BaseObserver
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxApplication
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.common.ImportStatusMessage
import info.mx.tracks.ops.AbstractOpSyncFromServerOperation
import info.mx.tracks.ops.OpSyncFromServerOperation
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage
import info.mx.tracks.sqlite.MxInfoDBOpenHelper
import info.mx.tracks.util.NetworkUtils
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * A list fragment representing a list of download items. This fragment also supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being viewed in a [FragmentDownloadDetail].
 *
 *
 * Activities containing this fragment MUST implement the [Callbacks] interface.
 */
class FragmentDownloadList : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var mCallbacks = emptyCallbacks
    private var mActivatedPosition = ListView.INVALID_POSITION
    private var mAdapter: SimpleCursorAdapter? = null
    private val projection = arrayOf(MxCalContract.DownLoadSite.DISPLAY, MxCalContract.DownLoadSite.CREATEDATE)
    private val to = intArrayOf(R.id.txDisplayText, R.id.txCreated)
    private var listview: ListView? = null
    private var lyProgress: View? = null
    private var tvProgress: TextView? = null

    private var viewDB: TextView? = null
    private var viewAndroidId: TextView? = null
    private var viewIPAddress: TextView? = null
    private var viewSwitches: TextView? = null
    private var viewRest: TextView? = null
    private var layoutInfo: LinearLayout? = null

    private val dataManagerAdmin: DataManagerAdmin by inject()

    internal interface Callbacks {
        fun onItemSelected(id: Long)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.screen_list_with_progressbar, null)

        listview = view.findViewById(R.id.list_overview)
        listview!!.itemsCanFocus = true // useless

        val emptyView = view.findViewById<TextView>(R.id.txt_no_displays)

        viewDB = view.findViewById(R.id.textVersionDB)
        viewRest = view.findViewById(R.id.textVersionRest)
        layoutInfo = view.findViewById(R.id.layoutInfo)
        viewAndroidId = view.findViewById(R.id.androidId)
        viewIPAddress = view.findViewById(R.id.IPAddress)
        viewSwitches = view.findViewById(R.id.switches)

        listview!!.emptyView = emptyView
        listview!!.setOnItemClickListener { _, _, _, id ->
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mCallbacks.onItemSelected(id)
        }

        // ImportProgress issue
        tvProgress = view.findViewById(R.id.textProgress)
        tvProgress!!.text = ""
        lyProgress = view.findViewById(R.id.lyImportProgress)
        lyProgress!!.visibility = View.GONE
        return view
    }

    @SuppressLint("HardwareIds", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
        }

        mAdapter = SimpleCursorAdapter(requireActivity(), R.layout.item_site_download, null, projection, to, 0)
        listview!!.adapter = mAdapter
        mAdapter!!.viewBinder = ViewBinderDownloadSite()

        // Setup menu using MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_download_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        lyProgress!!.visibility = View.GONE
        OpSyncFromServerOperation.importStatusMessage.observe(viewLifecycleOwner) { msg ->
            onImportStatusMessage(msg)
        }
        loaderManager.initLoader(LOADER_SITE, this.arguments, this)
        LoggingHelperAdmin.setMessage("")

        layoutInfo!!.visibility = View.VISIBLE
        viewDB!!.text = ""
        viewRest!!.text = ""
        viewAndroidId!!.text = Settings.Secure.getString(requireContext().applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        viewIPAddress!!.text = NetworkUtils.getIPAddress(true)
        viewSwitches!!.text = "Google:" + MxApplication.isGoogleTests +
                " Espresso:" + MxApplication.isRunningEspresso() +
                " Admin:" + MxCoreApplication.isAdmin +
                " Emulator:" + MxCoreApplication.isEmulator

        updateBackendInfo()

        val server = view.findViewById<Spinner>(R.id.spinnerBackend)
        for (i in 0 until server.count) {
            if (server.getItemAtPosition(i).toString() == CommLibPrefs.instance.serverUrl) {
                server.setSelection(i)
                break
            }
        }
        server.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val newServerUrl = parent.getItemAtPosition(position) as String
                if (CommLibPrefs.instance.serverUrl != newServerUrl) {
                    CommLibPrefs.instance.edit().putServerUrl(newServerUrl).commit()
                    updateBackendInfo()
                    lifecycleScope.launch {
                        MxCoreApplication.createApiClient()
                    }
                    MxCoreApplication.doSync(
                        updateProvider = false,
                        force = true,
                        flavor = BuildConfig.FLAVOR
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

    }

    private fun onImportStatusMessage(importStatusMessage: ImportStatusMessage) {
        if (importStatusMessage.message.isNotBlank()) {
            lyProgress!!.visibility = View.VISIBLE
        } else {
            lyProgress!!.visibility = View.GONE
        }
        tvProgress!!.text = importStatusMessage.message
    }

    private fun updateBackendInfo() {
        dataManagerAdmin.backendInfo.subscribe(object : BaseObserver<VersionInfo>(requireContext()) {
            @SuppressLint("SetTextI18n")
            override fun onNext(versionInfo: VersionInfo) {
                viewDB!!.text = versionInfo.dbVersion
                viewRest!!.text = versionInfo.restVersion + "  [" + versionInfo.dbName + "]"
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activities containing this fragment must implement its callbacks.
        check(context is Callbacks) { "Activity must implement fragment's callbacks." }

        mCallbacks = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        OpSyncFromServerOperation.importStatusMessage.removeObservers(this)
    }

    override fun onDetach() {
        super.onDetach()

        mCallbacks = emptyCallbacks
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState); crash !
        super.onSaveInstanceState(Bundle())
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    internal fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        listview!!.choiceMode = if (activateOnItemClick) ListView.CHOICE_MODE_SINGLE else ListView.CHOICE_MODE_NONE
    }

    private fun setActivatedPosition(position: Int) {
        if (position == ListView.INVALID_POSITION) {
            listview!!.setItemChecked(mActivatedPosition, false)
        } else {
            listview!!.setItemChecked(position, true)
        }
        mActivatedPosition = position
    }

    private fun handleMenuItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_solit_acceptable_tocheck) {
            val intentM = OpStageSplitOperation.newIntent(BuildConfig.FLAVOR)
            Ops.execute(intentM)

        } else if (i == R.id.menu_share) {
            val intentMail = BackupHelper.createShareIntent(
                requireContext(), requireActivity().getString(R.string.app_name),
                MxInfoDBOpenHelper.getDir(requireActivity()), MxInfoDBOpenHelper.VERSION
            )
            try {
                // intentMail.setType("message/rfc822");
                intentMail.putExtra(Intent.EXTRA_EMAIL, arrayOf("appdev.droider@googlemail.com"))
                intentMail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " DB dump")
                intentMail.putExtra(
                    Intent.EXTRA_TEXT,
                    "${TrackingApplication.getVersion(requireContext())}\nDB  Version: " + MxInfoDBOpenHelper.VERSION
                )
                startActivityForResult(Intent.createChooser(intentMail, "send debug info" + " ..."), SEND_MAIL)
            } catch (ex: android.content.ActivityNotFoundException) {
                Timber.e(ex)
            }

        } else if (i == R.id.menu_reset_reload_stage) {
            Trackstage.delete()
            Ops.execute(AbstractOpSyncFromServerOperation.newIntent(false, BuildConfig.FLAVOR))
        } else if (i == R.id.menu_facebook) {
            Ops.execute(OpFacebookSplitOperation.newIntent(BuildConfig.FLAVOR))
        } else if (i == R.id.menu_pictures) {
            val pictureIntent = Intent(activity, ActivityImageConfirm::class.java)
            startActivity(pictureIntent)
        } else if (i == R.id.menu_pictures_mxbrothers_stage) {
            val pictureStageIntent = Intent(activity, ActivityImageMXBrotherStage::class.java)
            startActivity(pictureStageIntent)
        }
        return true
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        when (loader) {
            LOADER_SITE -> {
                val query = SQuery.newQuery() // .expr(DownLoadSite., Op.EQ, arg);
                return query.createSupportLoader(MxCalContract.DownLoadSite.CONTENT_URI, null, MxCalContract.DownLoadSite.CREATEDATE + " desc")
            }

            else -> throw Exception("Importstatus removed")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_SITE -> mAdapter!!.swapCursor(cursor)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_SITE -> {
                mAdapter!!.swapCursor(null)
                lyProgress!!.visibility = View.GONE
            }
        }
    }

    companion object {

        private const val STATE_ACTIVATED_POSITION = "activated_position"
        private const val LOADER_SITE = 0
        private const val SEND_MAIL = 34

        private val emptyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(id: Long) {}
        }
    }
}
