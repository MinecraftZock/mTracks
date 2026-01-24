package info.mx.tracks.tracklist

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.LocationHelper
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.ImportStatusMessage
import info.mx.tracks.common.OverScrollListView
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.databinding.ScreenListWithProgressbarBinding
import info.mx.tracks.ops.OpSyncFromServerOperation
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
import java.util.Locale

/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
 */
class FragmentTrackList : FragmentBase(), LoaderManager.LoaderCallbacks<Cursor> {
    private var adapter: SimpleCursorAdapter? = null
    private var adapterTracksSort: AdapterTracksSort? = null
    private var callbacks = sEmptyCallbacks
    private var activatedPosition = ListView.INVALID_POSITION

    // If non-null, this is the current filter the user has provided.
    private var curFilter: String = ""

    private var isFav: Boolean = false
    private var sortOrder: String? = null
    private var viewBinder: ViewBinderTracks? = null
    private var searchView: SearchView? = null

    @SuppressLint("RestrictedApi")
    private var searchAutoComplete: SearchAutoComplete? = null
    private var locationCallback: LocationCallback? = null

    private val mxMemDatabase: MxMemDatabase by inject()

    private var _binding: ScreenListWithProgressbarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    internal interface Callbacks {
        fun onItemSelected(id: Long)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ScreenListWithProgressbarBinding.inflate(inflater, container, false)
        val view = binding.root


        sortOrder = if (arguments == null) {
            Tracksges.TRACKNAME
        } else {
            requireArguments().getString(FragmentUpDown.ORDER)!!.lowercase(Locale.getDefault())
        }

        // ImportProgress issue
        binding.lyImportProgress.visibility = View.GONE
        OpSyncFromServerOperation.importStatusMessage.observe(viewLifecycleOwner) { msg ->
            onImportStatusMessage(msg)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (context == null) {
                    return
                }
                for (location in locationResult.locations) {
                    val recalculateDistance = RecalculateDistance(requireContext())
                    recalculateDistance.recalculateTracks(location, "list")

                    viewBinder?.setMyLocation(location)
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        OpSyncFromServerOperation.importStatusMessage.removeObservers(this)
    }

    private fun onImportStatusMessage(importStatusMessage: ImportStatusMessage) {
        if (importStatusMessage.message.isNotBlank()) {
            binding.lyImportProgress.visibility = View.VISIBLE
        } else {
            binding.lyImportProgress.visibility = View.GONE
        }
        binding.containerProgress.textProgress.text = importStatusMessage.message
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup menu using MenuProvider
        setupMenu()

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(FILTER)) {
            curFilter = savedInstanceState.getString(FILTER)!!
            if (searchView != null) {
                searchAutoComplete?.setText(curFilter)
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
            adapterTracksSort = AdapterTracksSort(requireActivity())
            binding.listOverview.adapter = adapterTracksSort
        } else {
            adapter = SimpleCursorAdapter(
                requireActivity(),
                R.layout.item_track,
                null,
                ViewBinderTracks.projectionGesSum,
                ViewBinderTracks.toGesSum,
                0
            )
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

    protected fun setFilter2Fragment(filterString: String) {
        if (curFilter != filterString && isAdded) {
            curFilter = filterString
            if (curFilter.isNotBlank()) {
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
        } catch (_: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.no_email_installed), Toast.LENGTH_SHORT).show()
        }

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
                isFav = order == TracksGesSum.RATING.lowercase(Locale.getDefault())
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

            else -> throw Exception("Importstatus removed")
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
                Timber.i("onLoadFinished Tracks count:${cursor.count} loaderId:${loader.id}")
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_TRACKS -> adapter!!.swapCursor(null)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_track_list, menu)
                val searchItem = menu.findItem(R.id.menu_search)
                val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

                if (searchItem != null) {
                    searchView = searchItem.actionView as SearchView
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

                if (searchView != null && curFilter.isNotBlank()) {
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
                        return true
                    }

                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return true
                    }
                })

                if (null != searchView) {
                    searchView!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
                    searchView!!.setIconifiedByDefault(true)
                    searchView!!.setOnQueryTextListener(queryTextListener)
                    if (curFilter.isNotBlank()) {
                        if (searchAutoComplete != null) {
                            searchItem.expandActionView()
                            searchAutoComplete!!.setText(curFilter)
                        }
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add_track -> {
                        val qWfaddIntent = Intent(activity, ActivityTrackEdit::class.java)
                        startActivityForResult(qWfaddIntent, 1)
                        true
                    }

                    R.id.menu_filter -> {
                        val qWfIntent = Intent(activity, ActivityFilter::class.java)
                        startActivityForResult(qWfIntent, 2)
                        true
                    }

                    R.id.menu_filter_country -> {
                        val qWfcIntent = Intent(activity, ActivityFilterCountry::class.java)
                        startActivityForResult(qWfcIntent, 3)
                        true
                    }

                    R.id.menu_settings -> {
                        val qWfsIntent = Intent(activity, ActivitySetting::class.java)
                        startActivityForResult(qWfsIntent, 4)
                        true
                    }

                    R.id.menu_feedback -> {
                        doSendFeedBackMail()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onStart() {
        super.onStart()
        setUpLocationClientIfNeeded()
    }

    override fun onResume() {
        super.onResume()

        MxCoreApplication.doSync(false, false, BuildConfig.FLAVOR)


        if (adapterTracksSort == null) {
            loaderManager.restartLoader(LOADER_TRACKS, this.arguments, this)
        } else {
            //sort by distance
            addDisposable(
                mxMemDatabase
                    .tracksDistanceDao().all
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe { tracks -> adapterTracksSort!!.updateTracks(tracks) })
        }
    }

    override fun onPause() {
        if (adapterTracksSort == null) {
            loaderManager.destroyLoader(LOADER_TRACKS)
        }

        super.onPause()
        stopLocationUpdates()
    }

    private fun setUpLocationClientIfNeeded() {
        if (permissionHelper.hasLocationPermission()) {
            startLocationUpdates()
        } else if (sortOrder == Tracksges.TRACKNAME) { //otherwise it asks to often
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PermissionHelper.REQUEST_PERMISSION_LOCATION
            )
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (permissionHelper.hasLocationPermission()) {
            if (context != null) {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .requestLocationUpdates(LocationJobService.REQUEST_DAY, locationCallback!!, Looper.getMainLooper())

                // Get last known location
                if (isAdded) {
                    LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                        .addOnSuccessListener(requireActivity()) { last ->
                            if (last != null) {
                                viewBinder?.setMyLocation(last)
                            }
                        }
                }
            }
        }
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
        private const val FILTER = "FILTER"
        internal const val ONLY_FOREIGN = "only_foreign"
        private const val STATE_ACTIVATED_POSITION = "activated_position"

        private val sEmptyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(id: Long) {}
        }
    }
}
