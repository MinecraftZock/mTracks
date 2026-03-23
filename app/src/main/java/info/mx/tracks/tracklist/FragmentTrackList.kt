package info.mx.tracks.tracklist

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import info.mx.commonlib.LocationHelper
import info.mx.core.MxCoreApplication
import info.mx.core.common.ImportStatusMessage
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.core_generated.sqlite.MxInfoDBContract.TracksGesSum
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracksges
import info.mx.core_generated.sqlite.TracksGesSumRecord
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.SecHelper
import info.mx.tracks.databinding.ScreenListWithProgressbarBinding
import info.mx.tracks.ops.OpSyncFromServerOperation
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.memory.MxMemDatabase
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.settings.ActivityFilter
import info.mx.tracks.settings.ActivityFilterCountry
import info.mx.tracks.settings.ActivitySetting
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
class FragmentTrackList : FragmentBase() {
    private var tracksAdapter: TracksGesSumAdapter? = null
    private var adapterTracksSort: AdapterTracksSort? = null
    private var callbacks = sEmptyCallbacks
    private var activatedPosition = -1

    // If non-null, this is the current filter the user has provided.
    private var curFilter: String = ""

    private var isFav: Boolean = false
    private var sortOrder: String? = null
    private var searchView: SearchView? = null

    @SuppressLint("RestrictedApi")
    private var searchAutoComplete: SearchAutoComplete? = null
    private var locationCallback: LocationCallback? = null

    private val mxMemDatabase: MxMemDatabase by inject()
    private val mxDatabase: MxDatabase by inject()

    // Modern Activity Result API launchers
    private lateinit var trackDetailLauncher: ActivityResultLauncher<Intent>
    private lateinit var addTrackLauncher: ActivityResultLauncher<Intent>
    private lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private lateinit var filterCountryLauncher: ActivityResultLauncher<Intent>
    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestLocationPermissionsLauncher: ActivityResultLauncher<Array<String>>

    private var _binding: ScreenListWithProgressbarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    internal interface Callbacks {
        fun onItemSelected(id: Long)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register for activity results using modern API
        val resultCallback: (androidx.activity.result.ActivityResult) -> Unit = {
            // Reload the track list after returning from any activity - will happen automatically through Flow observation
        }

        trackDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), resultCallback)
        addTrackLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), resultCallback)
        filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), resultCallback)
        filterCountryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), resultCallback)
        settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), resultCallback)

        // Register for location permissions result
        requestLocationPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                startLocationUpdates()
            }
        }
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

                    tracksAdapter?.setMyLocation(location)
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
            searchView?.let {
                searchAutoComplete?.setText(curFilter)
                it.isIconified = false
                Timber.d("searchView %s", curFilter)
            }
        }

        // Setup RecyclerView or ListView based on sort order
        if (sortOrder == TracksGesSum.DISTANCE2LOCATION) {
            // Distance sorting still uses the legacy ListView adapter
            // TODO: Migrate AdapterTracksSort to RecyclerView.Adapter
            adapterTracksSort = AdapterTracksSort(requireContext(), this, mxDatabase)
            // For distance sorting, we temporarily skip setting the adapter
            // This needs to be properly fixed by migrating AdapterTracksSort
            Timber.w("Distance sorting with AdapterTracksSort - needs migration to RecyclerView")
        } else {
            tracksAdapter = TracksGesSumAdapter(
                context = requireContext(),
                onItemClick = { id, position ->
                    val isTablet = resources.getBoolean(R.bool.isTablet)
                    if (isTablet) {
                        callbacks.onItemSelected(id)
                    } else {
                        openDetail(id, position)
                    }
                },
                onItemLongClick = { id ->
                    val record = TracksGesSumRecord.get(id)
                    record?.let {
                        LocationHelper.openNavi(
                            context = this@FragmentTrackList.requireActivity(),
                    name = null,
                            latitude = SecHelper.entcryptXtude(it.latitude),
                            longitude = SecHelper.entcryptXtude(it.longitude)
                        )
                    }
                    true
                }
            )
            binding.listOverview.adapter = tracksAdapter
            binding.listOverview.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

            // Observe track data
            observeTrackData()
        }

        binding.containerProgress.textProgress.text = ""
    }

    @SuppressLint("SetTextI18n")
    private fun observeTrackData() {
        val order = arguments?.getString(FragmentUpDown.ORDER)?.lowercase(Locale.getDefault()) ?: Tracksges.TRACKNAME
        isFav = order == IS_FAVORITE
        val isOnlyForeign = arguments?.getBoolean(ONLY_FOREIGN, false) ?: false

        // Get shown countries for filtering
        val countries = mxDatabase.countryDao().allShown.map { it.country }

        // Observe tracks based on filter criteria
        val tracksFlow = if (isFav) {
            Timber.d("isFav true - observing favorite tracks with filter: $curFilter, order=$order")
            mxDatabase.trackDao().searchFavoriteTracksGesSum(
                searchText = curFilter,
                orderBy = order
            )
        } else {
            val countryFilter = if (countries.isEmpty()) "" else "filterIt"
            Timber.d("isFav=false - searchText: '$curFilter', countryFilter: $countryFilter, countries: $countries orderBy=$order")
            mxDatabase.trackDao().searchTracksGesSum(
                searchText = curFilter,
                countryFilter = countryFilter,
                countries = countries,
                orderBy = order
            )
        }

        tracksFlow.asLiveData().observe(viewLifecycleOwner) { tracks ->
            tracksAdapter?.submitList(tracks)

            // Update empty view
            if (tracks.isEmpty()) {
                binding.txtNoDisplays.visibility = View.VISIBLE
                val countriesStr = countries.joinToString(", ") { "\"$it\"" }
                val gesAnz = if (countriesStr.isEmpty()) 0 else tracks.size

                if (gesAnz == 0 && !isFav) {
                    binding.txtNoDisplays.text = getString(R.string.empty) + "\n" + getString(R.string.empty_countyselection)
                } else {
                    binding.txtNoDisplays.setText(R.string.empty)
                }
                if (!isFav) {
                    binding.txtNoDisplays.text = binding.txtNoDisplays.text.toString() + "\n" + getString(R.string.go_to_filter)
                }
            } else {
                binding.txtNoDisplays.visibility = View.GONE
            }

            Timber.i("observeTrackData: Tracks count:${tracks.size}")
        }
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

    private fun setActivatedPosition(position: Int) {
        if (position == -1) {
            // For RecyclerView, we don't use the same activation pattern as ListView
        } else {
            // For RecyclerView, we don't use the same activation pattern as ListView
        }
        activatedPosition = position
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
        outState.putString(FILTER, curFilter)
        super.onSaveInstanceState(Bundle())
        if (activatedPosition != -1) {
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
        trackDetailLauncher.launch(qWfIntent)
    }

    protected fun setFilter2Fragment(filterString: String) {
        if (curFilter != filterString && isAdded) {
            curFilter = filterString
            if (curFilter.isNotBlank()) {
                searchView!!.isIconified = false
            }
            // Trigger re-observation with new filter
            observeTrackData()
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
                        searchAutoComplete?.let {
                            searchItem.expandActionView()
                            it.setText(curFilter)
                        }
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add_track -> {
                        val qWfaddIntent = Intent(activity, ActivityTrackEdit::class.java)
                        addTrackLauncher.launch(qWfaddIntent)
                        true
                    }

                    R.id.menu_filter -> {
                        val qWfIntent = Intent(activity, ActivityFilter::class.java)
                        filterLauncher.launch(qWfIntent)
                        true
                    }

                    R.id.menu_filter_country -> {
                        val qWfcIntent = Intent(activity, ActivityFilterCountry::class.java)
                        filterCountryLauncher.launch(qWfcIntent)
                        true
                    }

                    R.id.menu_settings -> {
                        val qWfsIntent = Intent(activity, ActivitySetting::class.java)
                        settingsLauncher.launch(qWfsIntent)
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
            // Data is observed through LiveData/Flow - no need to manually restart loader
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
            // Data observation is lifecycle-aware - no need to manually destroy
        }

        super.onPause()
        stopLocationUpdates()
    }

    private fun setUpLocationClientIfNeeded() {
        if (permissionHelper.hasLocationPermission()) {
            startLocationUpdates()
        } else if (sortOrder == Tracksges.TRACKNAME) { //otherwise it asks to often
            requestLocationPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                            last?.let {
                                it
                                tracksAdapter?.setMyLocation(it)
                            }
                        }
                }
            }
        }
    }


    companion object {
        const val IS_FAVORITE = Tracks.TRACKNAME
        private const val FILTER = "FILTER"
        internal const val ONLY_FOREIGN = "only_foreign"
        private const val STATE_ACTIVATED_POSITION = "activated_position"

        private val sEmptyCallbacks: Callbacks = object : Callbacks {
            override fun onItemSelected(id: Long) {}
        }
    }
}
