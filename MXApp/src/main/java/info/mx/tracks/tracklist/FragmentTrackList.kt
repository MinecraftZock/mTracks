package info.mx.tracks.tracklist

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.LocationHelper
import info.mx.tracks.BuildConfig
import info.mx.tracks.DiskReceiver
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.OverScrollListView
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.databinding.ScreenListWithProgressbarBinding
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.memory.MxMemDatabase
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.settings.ActivityFilter
import info.mx.tracks.settings.ActivityFilterCountry
import info.mx.tracks.settings.ActivitySetting
import info.mx.tracks.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.tracks.sqlite.MxInfoDBContract.*
import info.mx.tracks.sqlite.TracksGesSumRecord
import info.mx.tracks.tools.PermissionHelper
import info.mx.tracks.trackdetail.ActivityTrackDetail
import info.mx.tracks.trackdetail.ActivityTrackEdit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
 */
class FragmentTrackList : FragmentBase(), LoaderManager.LoaderCallbacks<Cursor>, ConnectionCallbacks, OnConnectionFailedListener {
    private var adapter: SimpleCursorAdapter? = null
    private var adapterTracksSort: AdapterTracksSort? = null
    private var callbacks = sEmptyCallbacks
    private var activatedPosition = ListView.INVALID_POSITION

    // If non-null, this is the current filter the user has provided.
    private var curFilter: String? = null

    private var isFav: Boolean = false
    private var diskReceiver: DiskReceiver? = null
    private var sortOrder: String? = null
    private var viewBinder: ViewBinderTracks? = null
    private var googleApiClient: GoogleApiClient? = null
    private var searchView: SearchView? = null
    private var searchAutoComplete: SearchAutoComplete? = null
    private var locationCallback: LocationCallback? = null

    val mxMemDatabase: MxMemDatabase by inject()

    private var _binding: ScreenListWithProgressbarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val mxDatabase: MxDatabase by inject()

    internal interface Callbacks {
        fun onItemSelected(id: Long)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ScreenListWithProgressbarBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)

        sortOrder = if (arguments == null) {
            Tracksges.TRACKNAME
        } else {
            requireArguments().getString(FragmentUpDown.ORDER)!!.lowercase(Locale.getDefault())
        }

        // ImportProgress issue
        binding.lyImportProgress.visibility = View.GONE

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (context == null) {
                    return
                }
                for (location in locationResult.locations) {
                    val recalcDistance = RecalculateDistance(requireContext())
                    recalcDistance.recalculateTracks(location, "list")

                    viewBinder?.setMyLocation(location)
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(FILTER)) {
            curFilter = savedInstanceState.getString(FILTER)
            if (searchView != null) {
                if (searchAutoComplete != null) {
                    searchAutoComplete!!.setText(curFilter)
                }
                searchView!!.isIconified = false
                Timber.d("searchView %s", curFilter)
            }
        }

        // tablet makes items activate-able
        val isTablet = resources.getBoolean(R.bool.isTablet)
        setActivateOnItemClick(isTablet)

        binding.listOverview.itemsCanFocus = true// useless
        binding.listOverview.setOnOverScrollListener(object : OverScrollListView.OverScrollListener {
            override fun onOverScrollBy(
                deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int,
                maxOverScrollY: Int, isTouchEvent: Boolean
            ) {
                if (scrollY == -200) {
                    MxCoreApplication.doSync(updateProvider = true, force = true, flavor = BuildConfig.FLAVOR)
                }
            }

            override fun onOverScroll(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) = Unit
        })
        if (sortOrder == TracksGesSum.DISTANCE2LOCATION) {
            adapterTracksSort = AdapterTracksSort(requireActivity(), this, mxDatabase)
            binding.listOverview.adapter = adapterTracksSort
        } else {
            adapter =
                SimpleCursorAdapter(requireActivity(), R.layout.item_track, null, ViewBinderTracks.projectionGesSum, ViewBinderTracks.toGesSum, 0)
            binding.listOverview.adapter = adapter
            viewBinder = ViewBinderTracks(requireActivity(), null, true)
            adapter!!.viewBinder = viewBinder
        }

        binding.listOverview.emptyView = binding.txtNoDisplays
        binding.listOverview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, id ->
            if (isTablet) {
                callbacks.onItemSelected(id)
            } else {
                this@FragmentTrackList.openDetail(id, position)
            }
        }

        binding.listOverview.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, _, id ->
            val record = TracksGesSumRecord.get(id)
            if (record != null) {
                LocationHelper.openNavi(
                    this@FragmentTrackList.requireActivity(), null,
                    SecHelper.entcryptXtude(record.latitude),
                    SecHelper.entcryptXtude(record.longitude)
                )
            }
            true
        }

        binding.containerProgress.textProgress.text = ""
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Activities containing this fragment must implement its callbacks.
        check(context is Callbacks) { "Activity must implement fragment's callbacks." }

        callbacks = context
    }

    override fun onDetach() {
        super.onDetach()

        callbacks = sEmptyCallbacks
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    private fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        binding.listOverview.choiceMode = if (activateOnItemClick) ListView.CHOICE_MODE_SINGLE else ListView.CHOICE_MODE_NONE
    }

    private fun setActivatedPosition(position: Int) {
        if (position == ListView.INVALID_POSITION) {
            binding.listOverview.setItemChecked(activatedPosition, false)
        } else {
            binding.listOverview.setItemChecked(position, true)
        }
        activatedPosition = position
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
        outState.putString(FILTER, curFilter)
        super.onSaveInstanceState(Bundle())
        if (activatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition)
        }
    }

    protected fun openDetail(id: Long, position: Int) {
        val qWfIntent = Intent(activity, ActivityTrackDetail::class.java)
        val bundle = Bundle()
        Timber.d("open:$id")
        bundle.putLong(FragmentUpDown.RECORD_ID_LOCAL, id)
        bundle.putString(FragmentUpDown.CONTENT_URI, TracksGesSum.CONTENT_URI.toString())
        bundle.putString(FragmentUpDown.FILTER, curFilter)
        if (this@FragmentTrackList.arguments != null) {
            bundle.putString(
                FragmentUpDown.ORDER, this@FragmentTrackList.requireArguments().getString(FragmentUpDown.ORDER)!!
                    .lowercase(Locale.getDefault())
            )
        }
        bundle.putInt(FragmentUpDown.CURSOR_POSITION, position)
        qWfIntent.putExtras(bundle)
        startActivityForResult(qWfIntent, 1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_fragment_track_list, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            //            searchView.setQueryHint(getString(R.string.text));
        }
        if (searchView != null) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        }

        if (searchView == null) {
            return
        }
        searchAutoComplete = searchView!!.findViewById(R.id.search_src_text)
        if (searchAutoComplete != null) {
            searchAutoComplete!!.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.abc_secondary_text_material_light))
        }

        if (searchView != null && curFilter != null && curFilter != "") {
            if (searchAutoComplete != null) {
                searchAutoComplete!!.setText(curFilter)
            }
            searchView!!.isIconified = false
        } else {
            if (searchAutoComplete != null) {
                searchAutoComplete!!.setText("")
            }
            searchView!!.isIconified = true
        }

        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != "") {
                    setFilter2Fragment(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                setFilter2Fragment(query)
                return true
            }
        }
        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                setFilter2Fragment("")
                return true  // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                return true  // Return true to expand action view
            }
        })

        if (null != searchView) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            searchView!!.setIconifiedByDefault(true)
            searchView!!.setOnQueryTextListener(queryTextListener)
            if (curFilter != null && curFilter != "") {
                if (searchAutoComplete != null) {
                    searchItem.expandActionView()
                    //                    searchView.setIconified(false);
                    searchAutoComplete!!.setText(curFilter)
                }
            }
        }
    }

    fun onBackPressed(): Boolean {
        var backPressed = true
        if (searchView != null && !searchView!!.isIconified) {
            if (searchAutoComplete != null) {
                searchAutoComplete!!.setText("")
            }
            searchView!!.isIconified = true
            backPressed = false
            setFilter2Fragment("")
        }
        return backPressed
    }

    protected fun setFilter2Fragment(filterString: String) {
        if (curFilter == null) {
            curFilter = ""
        }

        if (curFilter != filterString && isAdded) {
            curFilter = filterString
            if (curFilter != null && curFilter != "") {
                searchView!!.isIconified = false
            }
            loaderManager.restartLoader(LOADER_TRACKS, this@FragmentTrackList.arguments, this@FragmentTrackList)
        }
    }

    private fun doSendFeedBackMail() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("appdev.droider@gmail.com"))
        i.putExtra(Intent.EXTRA_SUBJECT, "MX Tracks Info feedback")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.no_email_installed), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_add_track) {
            val qWfaddIntent = Intent(activity, ActivityTrackEdit::class.java)
            startActivityForResult(qWfaddIntent, 1)
        } else if (item.itemId == R.id.menu_filter) {
            val qWfIntent = Intent(activity, ActivityFilter::class.java)
            startActivityForResult(qWfIntent, 2)
        } else if (item.itemId == R.id.menu_filter_country) {
            val qWfcIntent = Intent(activity, ActivityFilterCountry::class.java)
            startActivityForResult(qWfcIntent, 3)
        } else if (item.itemId == R.id.menu_settings) {
            val qWfsIntent = Intent(activity, ActivitySetting::class.java)
            startActivityForResult(qWfsIntent, 4)
        } else if (item.itemId == R.id.menu_feedback) {
            doSendFeedBackMail()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loaderManager.restartLoader(LOADER_TRACKS, this@FragmentTrackList.arguments, this@FragmentTrackList)
    }

    override fun onCreateLoader(id: Int, bundleIn: Bundle?): Loader<Cursor> {
        var bundle = bundleIn
        when (id) {
            LOADER_TRACKS -> {
                if (bundle == null) {
                    bundle = Bundle()
                }
                val order = bundle.getString(FragmentUpDown.ORDER)!!.lowercase(Locale.getDefault())
                Timber.i("onCreateLoader $order $curFilter")
                isFav = order == IS_FAVORITE
                val isStage = order == TracksGesSum.APPROVED.lowercase(Locale.getDefault())
                val isOnlyForeign = bundle.getBoolean(ONLY_FOREIGN, false)
                var query = SQuery.newQuery()
                if (isStage) {
                    query = QueryHelper.buildStageFilter(query, isOnlyForeign)
                } else {
                    query = QueryHelper.buildUserTrackSearchFilter(query, curFilter, isFav, AbstractMxInfoDBOpenHelper.Sources.TRACKS_GES_SUM)
                }
                return query.createSupportLoader(TracksGesSum.CONTENT_URI, null, order)
            }
//            LOADER_PROGRESS
            else -> return SQuery.newQuery().createSupportLoader(Importstatus.CONTENT_URI, null, Importstatus.CREATED + " desc")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_TRACKS -> {
                val order = requireArguments().getString(FragmentUpDown.ORDER)!!.lowercase(Locale.getDefault())
                if (order != TracksGesSum.DISTANCE2LOCATION) {
                    adapter!!.swapCursor(cursor)
                }
                if (cursor.count == 0) {
                    val gesAnz = SQuery.newQuery()
                        .append(" " + TracksGesSum.COUNTRY + " IN (select " + Country.COUNTRY + " from country where show=?)", "1")
                        .firstInt(TracksGesSum.CONTENT_URI, "max(" + TracksGesSum._ID + ")")
                    if (gesAnz == 0 && !isFav) {
                        binding.txtNoDisplays.text = getString(R.string.empty) + "\n" + getString(R.string.empty_countyselection)
                    } else {
                        binding.txtNoDisplays.setText(R.string.empty)
                    }
                    if (!isFav) {
                        binding.txtNoDisplays.text = binding.txtNoDisplays.text.toString() + "\n" + getString(R.string.go_to_filter)
                    }
                }
                Timber.i("onLoadFinished Tracks:${cursor.count} loader:${loader.id}")
            }

            LOADER_PROGRESS -> {
                cursor.moveToFirst()
                if (cursor.count > 0 && cursor.getString(cursor.getColumnIndex(Importstatus.MSG)) != null &&
                    cursor.getString(cursor.getColumnIndex(Importstatus.MSG)) != ""
                ) {
                    binding.lyImportProgress.visibility = View.VISIBLE
                    // showImportElements(400);
                    val txt = cursor.getString(cursor.getColumnIndex(Importstatus.MSG))
                    binding.containerProgress.textProgress.text = txt
                } else {
                    binding.lyImportProgress.visibility = View.GONE
                    //                    hideImportElements(400);
                    binding.containerProgress.textProgress.text = ""
                    Timber.i("loader hide")
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_TRACKS -> if (sortOrder != TracksGesSum.DISTANCE2LOCATION) {
                adapter!!.swapCursor(null)
            }

            LOADER_PROGRESS -> binding.lyImportProgress.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        setUpLocationClientIfNeeded()
    }

    override fun onResume() {
        super.onResume()

        MxCoreApplication.doSync(false, false, BuildConfig.FLAVOR)

        diskReceiver = DiskReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(diskReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else
            requireActivity().registerReceiver(diskReceiver, filter)

        if (adapterTracksSort == null) {
            loaderManager.restartLoader(LOADER_TRACKS, this.arguments, this)
            loaderManager.initLoader(LOADER_PROGRESS, arguments, this)
        } else {
            //sort by distance
            addDisposable(mxMemDatabase
                .tracksDistanceDao().all
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { tracks -> adapterTracksSort!!.updateTracks(tracks) })
        }
    }

    override fun onPause() {
        if (adapterTracksSort == null) {
            loaderManager.destroyLoader(LOADER_TRACKS)
            loaderManager.destroyLoader(LOADER_PROGRESS)
            //} else {
            //    //sort by distance
        }

        super.onPause()
        if (diskReceiver != null) {
            try {
                requireActivity().unregisterReceiver(diskReceiver)
            } catch (ignored: Exception) {
            }

        }
        stopLocationUpdates()
    }

    private fun setUpLocationClientIfNeeded() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        }
        if (permissionHelper.hasLocationPermission()) {
            googleApiClient!!.connect()
        } else if (sortOrder == Tracksges.TRACKNAME) { //otherwise it asks to often
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PermissionHelper.REQUEST_PERMISSION_LOCATION
            )
        }
    }

    private fun stopLocationUpdates() {
        if (googleApiClient!!.isConnected) {
            LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(locationCallback!!)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (Objects.requireNonNull(permissionHelper).hasLocationPermission()) {
            if (context != null) {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .requestLocationUpdates(LocationJobService.REQUEST_DAY, locationCallback!!, Looper.getMainLooper())
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {}

    @SuppressLint("MissingPermission")
    override fun onConnected(connectionHint: Bundle?) {
        startLocationUpdates()

        if (isAdded) {
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                .addOnSuccessListener(requireActivity()) { last ->
                    if (last != null) {
                        if (viewBinder != null) {
                            viewBinder!!.setMyLocation(last)
                        }
                    }
                }
        }
    }

    override fun onConnectionSuspended(arg0: Int) {
        // nothing
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var granted = false
        when (requestCode) {
            PermissionHelper.REQUEST_PERMISSION_LOCATION -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED && permissions[i].endsWith("LOCATION")) {
                        granted = true
                    }
                }
                if (granted) {
                    // We can now safely use the API we requested access to
                    LocationJobService.restartService(requireActivity())
                    setUpLocationClientIfNeeded()
                }
            }
        }
    }

    companion object {
        const val IS_FAVORITE = Tracks.TRACKNAME
        private const val LOADER_TRACKS = 0
        private const val LOADER_PROGRESS = 1
        private const val FILTER = "FILTER"
        internal const val ONLY_FOREIGN = "only_foreign"
        private const val STATE_ACTIVATED_POSITION = "activated_position"

        private val sEmptyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(id: Long) {}
        }
    }
}
