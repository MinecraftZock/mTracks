package info.mx.tracks.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.androidmapsextensions.*
import com.androidmapsextensions.GoogleMap.OnCameraChangeListener
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.robotoworks.mechanoid.db.SQuery
import com.sothree.slidinguppanel.PanelSlideListener
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.StageHelperExtension
import info.mx.tracks.databinding.FragmentMapBinding
import info.mx.tracks.map.MapButtonsOverlay.MapOverlayButtonsListener
import info.mx.tracks.map.PoiDetailHeaderView.PoiDetailHeaderListener
import info.mx.tracks.map.PoiDetailHeaderView.PoiDetailStyle
import info.mx.tracks.map.cluster.MapClusterOptionsProvider
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.rest.google.Routes
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.settings.ActivityFilter
import info.mx.tracks.sqlite.*
import info.mx.tracks.sqlite.MxInfoDBContract.*
import info.mx.tracks.tools.AddMobHelper
import info.mx.tracks.tools.PermissionHelper
import info.mx.tracks.trackdetail.ActivityTrackEdit
import info.mx.tracks.trackdetail.FragmentPlaceDetail
import info.mx.tracks.trackdetail.FragmentTrackDetail
import info.mx.tracks.trackdetail.FragmentTrackDetailTab
import info.mx.tracks.tracklist.ViewBinderTracks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
abstract class BaseFragmentMap : FragmentMapBase(), MapOverlayButtonsListener, LoaderManager.LoaderCallbacks<Cursor>, ConnectionCallbacks,
    OnConnectionFailedListener, OnMarkerClickListener, OnCameraChangeListener, PoiDetailHeaderListener {
    private var srcText = ""
    private var savePos: CameraPosition? = null

    private val addMobHelper: AddMobHelper by inject()

    val mxDatabase: MxDatabase by inject()

    private var interestLatLng: LatLng? = null
    private var googleApiClient: GoogleApiClient? = null
    private var prefMapLayerListener: OnSharedPreferenceChangeListener? = null
    private var headerView: PoiDetailHeaderView? = null
    protected var slidingDrawer: SlidingUpPanelLayout? = null
    private var searchView: SearchView? = null
    private var menuSearch: MenuItem? = null
    private var searchListLayout: LinearLayout? = null
    private var searchList: ListView? = null
    private var searchAdapter: SimpleCursorAdapter? = null
    private var slidingBody: View? = null
    private var searchItemClick = false
    private var polylineRoute: Polyline? = null
    private var currId: Long = 0
    private var currZoom = 0f
    private var polylineSurveillance: Polyline? = null
    private var tvProgress: TextView? = null
    private var lyProgress: View? = null
    private var viewFilterActive: ImageView? = null
    private var isKeyboardActive = false
    private var inPlaceSearch = false
    private var searchAutoComplete: SearchAutoComplete? = null

    private val poiPanelSlideListener = object : PanelSlideListener {

        override fun onPanelSlide(panel: View, slideOffset: Float) {
            val params = slidingBody!!.layoutParams
            params.height = (panel.measuredHeight * slideOffset).roundToInt() // panel.getHeight(); //-height;
            slidingBody!!.layoutParams = params
            Timber.d("${panel.height}/${params.height} offset:$slideOffset")
            setFabPosition(PanelState.DRAGGING, slideOffset)
        }

        override fun onPanelStateChanged(panel: View, previousState: PanelState, newState: PanelState) {
            val heightHeader = panel.findViewById<View>(R.id.layoutPoiHeader).height
            when (newState) {
                PanelState.ANCHORED -> {
                    closeSearch()
                    if (activity != null) {
                        requireActivity().invalidateOptionsMenu()
                    }

                    val params = slidingBody!!.layoutParams
                    params.height = panel.height //-height;
                    Timber.d(panel.height.toString() + "-" + heightHeader + "=" + params.height)
                    setFabPosition(PanelState.ANCHORED, 0f)
                }

                PanelState.COLLAPSED -> if (isAdded) {
                    Timber.d("${panel.id} height:${panel.height} new:$heightHeader")
                    slidingDrawer!!.panelHeight = heightHeader
                    if (activity != null) {
                        requireActivity().invalidateOptionsMenu()
                    }
                    setFabPosition(PanelState.COLLAPSED, 0f)
                }

                PanelState.DRAGGING -> {
                }

                PanelState.EXPANDED -> {
                    Timber.d("onPanelExpanded() %s", panel.height)
                    if (activity != null) {
                        requireActivity().invalidateOptionsMenu()
                    }
                    setFabPosition(PanelState.EXPANDED, 0f)
                }

                PanelState.HIDDEN -> {
                    Timber.d("onPanelHidden()")
                    setFabPosition(PanelState.HIDDEN, 0f)
                }
            }
        }
    }
    private var lastInterstitial: Long = 0
    private val interstitialHandler = Handler(Looper.getMainLooper())
    private val visibleMarkers = ArrayList<Marker>()
    private var dragTargetId: Long = 0L
    private var mLocationCallback: LocationCallback? = null

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("RtlHardcoded")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        viewFilterActive = view.findViewById(R.id.imageFilterActive)
        headerView = view.findViewById(R.id.slidePoiHeader)
        slidingDrawer = view.findViewById(R.id.sliding_layout)
        slidingBody = view.findViewById(R.id.fragment_container_map)
        searchList = view.findViewById(R.id.listSearch)
        searchListLayout = view.findViewById(R.id.layoutSearchResult)
        slidingDrawer!!.addPanelSlideListener(poiPanelSlideListener)

        // ImportProgress issue
        tvProgress = view.findViewById(R.id.textProgress)
        tvProgress!!.text = ""
        lyProgress = view.findViewById(R.id.lyImportProgress)
        lyProgress!!.visibility = View.GONE

        searchList!!.setOnItemClickListener { parent, view1, position, id ->
            if (searchList!!.adapter is AdapterPlaceAutocomplete) {
                val placeAdapter = searchList!!.adapter as AdapterPlaceAutocomplete
                val markerPlace = placeAdapter.getMarker(position)
                searchItemClick = true
                slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
                onMarkerSelect(markerPlace, map!!.maxZoomLevel - 7)
            } else {
                val marker = map?.getTrackMarker(id)
                if (marker != null) {
                    searchItemClick = true
                    slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
                    onMarkerSelect(marker, map!!.maxZoomLevel - 7)
                }
            }
        }

        viewFilterActive!!.setOnClickListener { v ->
            val toast = Toast.makeText(activity, R.string.filter_is_active, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.LEFT, 0, 0)
            toast.show()
        }
        viewFilterActive!!.visibility = if (QueryHelper.isFiltered()) View.VISIBLE else View.GONE
        headerView!!.setCallBackFragment(this)
        setHasOptionsMenu(true)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRACK_CLIENT_ID)) {
                currId = savedInstanceState.getLong(TRACK_CLIENT_ID)
                currZoom = savedInstanceState.getFloat(CURR_ZOOM)
            }
        }

        binding.mapButtonsOvelay.setListener(this)
        val prefs = MxPreferences.getInstance()

        prefMapLayerListener = OnSharedPreferenceChangeListener { prefsKind, key ->
            if (key == MxPreferences.Keys.MAP_TYPE) {
                this@BaseFragmentMap.setMapTypeFromPrefs()
            }
            viewFilterActive!!.visibility = if (QueryHelper.isFiltered()) View.VISIBLE else View.GONE
            if (this@BaseFragmentMap.isAdded && this@BaseFragmentMap.map != null) {
                this@BaseFragmentMap.loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(prefMapLayerListener)

        if (requireActivity().intent != null && requireActivity().intent.extras != null) {
            if (requireActivity().intent.extras!!.containsKey(ActivityMapExtension.LAT)) {
                val lat = requireActivity().intent.extras!!.getDouble(ActivityMapExtension.LAT)
                val lon = requireActivity().intent.extras!!.getDouble(ActivityMapExtension.LON)
                interestLatLng = LatLng(lat, lon)
            }
        } else if (prefs.mapLatitude != 0f) {
            interestLatLng = LatLng(prefs.mapLatitude.toDouble(), prefs.mapLongitude.toDouble())
        }

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (context == null) {
                    return
                }
                for (location in locationResult.locations) {
                    val recalculateDistance = RecalculateDistance(requireContext())
                    recalculateDistance.recalculateTracks(location, "map")

                    headerView!!.setLocation(location)
                }
            }
        }

    }

    private fun setMapTypeFromPrefs() {
        if (map != null) {
            when (MxPreferences.getInstance().mapType) {
                0 -> map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                1 -> map!!.mapType = GoogleMap.MAP_TYPE_HYBRID
                2 -> map!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
                3 -> map!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
        }
    }

    private fun closeSearch() {
        if (searchView != null) {
            if (searchAutoComplete != null) {
                searchAutoComplete!!.setText("")
            }
            if (!searchView!!.isIconified) {
                searchView!!.isIconified = true
                menuSearch!!.collapseActionView()
            }
            setSearchListVisibility(View.GONE)
        }
    }

    fun onBackPressed(): Boolean {
        Timber.d("onBackPressed()")
        if (searchView != null && !searchView!!.isIconified && activity != null) {
            if (slidingDrawer!!.getPanelState() != PanelState.HIDDEN) {
                if (isKeyboardActive) {
                    hideKeyBoard()
                } else {
                    requireActivity().invalidateOptionsMenu()
                    setSliderVisibility(View.GONE)
                    closeSearch()
                }
            } else if (slidingDrawer!!.getPanelState() == PanelState.HIDDEN) {
                requireActivity().invalidateOptionsMenu()
                setSliderVisibility(View.GONE)
                closeSearch()
            }
            return false
        } else if (slidingDrawer!!.getPanelState() != PanelState.HIDDEN) {
            requireActivity().invalidateOptionsMenu()
            setSliderVisibility(View.GONE)
            closeSearch()
            return false
        } else {
            return true
        }

    }

    private fun setSliderVisibility(visible: Int) {
        val open = slidingDrawer!!.getPanelState() == PanelState.EXPANDED || slidingDrawer!!.getPanelState() == PanelState.ANCHORED
        if (visible != View.VISIBLE && open) {
            slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
        } else if (visible == View.VISIBLE && slidingDrawer!!.getPanelState() == PanelState.HIDDEN) {
            slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
        } else if (visible == View.VISIBLE && slidingDrawer!!.getPanelState() != PanelState.COLLAPSED) {
            slidingDrawer!!.setPanelState(PanelState.ANCHORED)
        } else if (visible == View.GONE) {
            slidingDrawer!!.setPanelState(PanelState.HIDDEN)
            loaderManager.destroyLoader(LOADER_ROUTE)
            currId = 0
        }
    }

    override fun setUpMap() {
        if (MxCoreApplication.isAdmin) {
            drawSurveillancePolyline()
        }

        setMapTypeFromPrefs()

        map!!.setOnMarkerClickListener(this)
        map!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {

                val currentScreen = map!!.projection.visibleRegion.latLngBounds
                for (mapMarker in map!!.markers) {
                    if (mapMarker.getData<Any>() != null && mapMarker.getData<Any>() is Long) {
                        if (currentScreen.contains(mapMarker.position)) {
                            visibleMarkers.add(mapMarker)
                        }
                    }
                }
            }

            override fun onMarkerDrag(marker: Marker) {

                val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val projection = map!!.projection
                val markerLocation = marker.position
                val screenPosition = projection.toScreenLocation(markerLocation)
                for (mapMarker in visibleMarkers) {
                    val markerAllLocation = mapMarker.position
                    val screeAllPosition = projection.toScreenLocation(markerAllLocation)

                    val diffX = abs(screenPosition.x - screeAllPosition.x)
                    val diffY = abs(screenPosition.y - screeAllPosition.y)
                    if (diffX < TOLERANCE && diffY < TOLERANCE) {

                        if (Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(100)
                        }

                        Timber.d("${mapMarker.getData<Any>()}$diffX / $diffY")
                        dragTargetId = mapMarker.getData()
                        break
                    } else {
                        dragTargetId = 0L
                    }
                }
            }

            override fun onMarkerDragEnd(marker: Marker) {
                if (dragTargetId != 0L && marker.getData<Any>() != null && marker.getData<Any>() is TrackstageRecord) {
                    val trackstageRecord = marker.getData<TrackstageRecord>()
                    askForMergeDlg(dragTargetId, trackstageRecord)
                }
            }
        })
        map!!.isTrafficEnabled = MxPreferences.getInstance().mapTraffic
        if (permissionHelper.hasLocationPermission()) {
            map!!.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PermissionHelper.REQUEST_PERMISSION_LOCATION
            )
        }
        map!!.uiSettings.isZoomControlsEnabled = true
        map!!.uiSettings.isMapToolbarEnabled = false
        map!!.setOnMapLoadedCallback {
            if (searchView != null) {
                searchView!!.visibility = View.VISIBLE
            }
            if (isAdded) {
                loaderManager.initLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
                if (interestLatLng != null) {
                    zoomToInterestLocation()
                }
            }
        }

        updateClustering(true)
    }

    private fun askForMergeDlg(targetId: Long?, trackstageRecord: TrackstageRecord) {
        val tracksRecord = TracksRecord.get(targetId!!)
        val myAlertDialog = AlertDialog.Builder(requireActivity())
        myAlertDialog.setTitle(R.string.info)
        myAlertDialog.setMessage(trackstageRecord.trackname + " -> " + tracksRecord!!.trackname)
        myAlertDialog.setPositiveButton(R.string.yes) { dialog, which -> updateTrackstage(targetId, trackstageRecord) }
        myAlertDialog.setNegativeButton(android.R.string.cancel) { arg0, arg1 ->
            // do something when the Cancel button is clicked
        }
        myAlertDialog.show()
    }

    private fun updateTrackstage(targetId: Long?, trackstageRecord: TrackstageRecord) {
        val tracksRecord = TracksRecord.get(targetId!!)
        trackstageRecord.trackRestId = tracksRecord!!.restId
        trackstageRecord.updated = 1
        trackstageRecord.save(false)
        MxCoreApplication.doSync(true, true, BuildConfig.FLAVOR)
    }

    private fun openTrackInSlider(id: Long) {
        headerView!!.showTrackDetail(id, loaderManager)
        if (activity != null) {
            requireActivity().invalidateOptionsMenu()
        }

        val arguments = Bundle()
        arguments.putLong(FragmentUpDown.RECORD_ID_LOCAL, id)
        arguments.putString(FragmentUpDown.CONTENT_URI, Tracksges.CONTENT_URI.toString())
        arguments.putBoolean(FragmentTrackDetail.IN_SLIDER, true)
        val fragmentTrackDetailTab = FragmentTrackDetailTab()
        fragmentTrackDetailTab.arguments = arguments

        // Replace in the fragment_container_map view with this fragment,
        // add the transaction to the back stack so the user can navigate back
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container_map, fragmentTrackDetailTab, FragmentTrackDetailTab.TAG)
            .addToBackStack(null)
            .commitAllowingStateLoss()

        //FIXME slidingDrawer.setScrollableView(fragmentTrackDetail.getScrollView());
        currId = id
        val bundle = Bundle()
        bundle.putLong(TRACK_CLIENT_ID, id)
        loaderManager.restartLoader(LOADER_ROUTE, bundle, this)
    }

    private fun openPlaceInSlider(mxPlace: MxPlace) {
        headerView!!.showPlaceDetail(mxPlace)
        if (activity != null) {
            requireActivity().invalidateOptionsMenu()
        }

        val arguments = Bundle()
        arguments.putParcelable(FragmentPlaceDetail.PLACE, mxPlace)
        val fragmentPlace = FragmentPlaceDetail()
        fragmentPlace.arguments = arguments

        // Replace in the fragment_container_map view with this fragment,
        // add the transaction to the back stack so the user can navigate back
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container_map, fragmentPlace)
            .addToBackStack(null)
            .commitAllowingStateLoss()

        slidingDrawer!!.scrollableView = fragmentPlace.scrollContent
    }

    protected open fun openStageInSlider(id: Long) {
        headerView!!.showStageDetail(id, loaderManager)
        if (activity != null) {
            requireActivity().invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        super.onResume()

        loaderManager.initLoader(LOADER_PROGRESS, arguments, this)

        MxCoreApplication.doSync(false, false, BuildConfig.FLAVOR)

        setUpLocationClientIfNeeded()
    }

    private fun drawSurveillancePolyline() {
        addDisposable(mxDatabase.capturedLatLngDao().allNonIgnored
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { capturedLatLngs ->
                if (polylineSurveillance != null) {
                    polylineSurveillance!!.remove()
                }
                try {
                    val decodedPath = ArrayList<LatLng>()
                    for ((_, lat, lon) in capturedLatLngs) {
                        decodedPath.add(LatLng(lat, lon))
                    }
                    polylineSurveillance = map!!.addPolyline(
                        PolylineOptions().addAll(decodedPath)
                            .width(10f)
                            .color(ContextCompat.getColor(requireContext(), R.color.black))
                    )
                } catch (ignored: NullPointerException) {
                }
            })
    }

    override fun onPause() {
        super.onPause()
        if (searchAutoComplete != null) {
            searchAutoComplete!!.setText("")
        }
        if (googleApiClient!!.isConnected) {
            LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(mLocationCallback!!)
        }
        loaderManager.destroyLoader(LOADER_PROGRESS)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(TRACK_CLIENT_ID, currId)
        if (map != null) {
            outState.putFloat(CURR_ZOOM, map!!.cameraPosition.zoom)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchAdapter = SimpleCursorAdapter(context, R.layout.item_track, null, ViewBinderTracks.projectionGes, ViewBinderTracks.toGes, 0)
        val viewBinder = ViewBinderTracks(context, null, false)
        searchAdapter!!.viewBinder = viewBinder
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (Objects.requireNonNull(permissionHelper).hasLocationPermission()) {
            if (context != null) {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .requestLocationUpdates(LocationJobService.REQUEST_DAY, mLocationCallback!!, Looper.getMainLooper())
            }
        }
    }

    private fun setUpLocationClientIfNeeded() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        }
        googleApiClient!!.connect()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_map, menu)

        // setup of searchView in actionbar
        if (map == null) {
            return
        }
        menuSearch = menu.findItem(R.id.menu_search)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu.findItem(R.id.menu_search).actionView as SearchView).apply {
            visibility = View.GONE
            setIconifiedByDefault(true)
            isSubmitButtonEnabled = true
            isIconified = true // to be opened collapsed
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        }

        if (menuSearch != null) {
            searchView = menuSearch!!.actionView as SearchView
        }
        if (searchView != null) {
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        } else {
            return
        }

        searchAutoComplete = searchView!!.findViewById(R.id.search_src_text)
        if (searchAutoComplete != null) {
            searchAutoComplete!!.setHintTextColor(ContextCompat.getColor(requireActivity(), R.color.abc_secondary_text_material_light))
        }

        val mGoButton = searchView!!.findViewById<ImageView>(R.id.search_go_btn)
        mGoButton.setImageResource(android.R.drawable.ic_menu_search)

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(txt: String): Boolean {
                if (txt.isEmpty() && srcText != "") {
                    Timber.d("reset text")
                    srcText = ""
                    loaderManager.destroyLoader(LOADER_TRACKS)
                    loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
                } else if (txt.length >= MIN_TEXT_SEARCH_LENGTH) {
                    Timber.d("set:$txt")
                    val bundle = Bundle()
                    bundle.putString(SEARCH_TEXT, txt)
                    loaderManager.destroyLoader(LOADER_TRACKS)
                    loaderManager.restartLoader(LOADER_TRACKS, bundle, this@BaseFragmentMap)
                    srcText = txt
                }
                inPlaceSearch = false
                return false
            }

            override fun onQueryTextSubmit(txt: String): Boolean {

                MxCoreApplication.trackEvent("search", txt)
                val bounds = map!!.projection.visibleRegion.latLngBounds
                val mAdapter = AdapterPlaceAutocomplete(this@BaseFragmentMap, map!!, bounds, txt)

                searchList!!.adapter = mAdapter
                hideKeyBoard()

                inPlaceSearch = true
                //show all tracks
                loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
                return false
            }
        })

        searchView!!.setOnCloseListener {
            Timber.d("animate  :%s", savePos)
            savePos?.let {
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(it)
                map!!.animateCamera(cameraUpdate)
                Timber.d("animateCameraSave %s", savePos)
                loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
            }
            setSearchListVisibility(View.GONE)
            inPlaceSearch = false
            false
        }

        searchView!!.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showKeyboard()
                if (savePos == null) {
                    savePos = map!!.cameraPosition
                }
                Timber.d("hasFocus :true animateSave:$savePos")
                slidingDrawer!!.setPanelState(PanelState.HIDDEN)
                setSearchListVisibility(View.VISIBLE)
                map?.removeAllPlacesMarker()
            } else {
                if (!searchItemClick) {
                    if (isKeyboardActive) {
                        hideKeyBoard()
                    } else {
                        savePos?.let {
                            val cameraUpdate = CameraUpdateFactory.newCameraPosition(it)
                            map!!.animateCamera(cameraUpdate)
                        }
                        Timber.d("animateCameraSave2 %s", savePos)
                        loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
                    }
                } else {
                    closeSearch()
                    loaderManager.restartLoader(LOADER_TRACKS, null, this@BaseFragmentMap)
                }

            }
            searchItemClick = false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setFabPosition(panelState: PanelState, position: Float) {
        if (activity is ActivityMapExtension) {
            (activity as ActivityMapExtension).setFabPosition(panelState, slidingDrawer!!.panelHeight, position)
        }
    }

    private fun setSearchListVisibility(visibility: Int) {
        searchListLayout!!.visibility = visibility
        if (map != null) {
            if (visibility == View.VISIBLE) {
                binding.mapButtonsOvelay.visibility = View.GONE
            } else {
                binding.mapButtonsOvelay.visibility = View.VISIBLE
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val open = slidingDrawer!!.getPanelState() == PanelState.EXPANDED || slidingDrawer!!.getPanelState() == PanelState.ANCHORED

        val stageOpen = slidingDrawer!!.getPanelState() != PanelState.HIDDEN && headerView!!.detailStyle == PoiDetailStyle.DETAIL_STAGE

        Timber.d("slider open:$open ${slidingDrawer!!.getPanelState()} stageOpen:$stageOpen")
        menu.findItem(R.id.menu_search).isVisible = !open && !stageOpen
        menu.findItem(R.id.menu_map2_filter).isVisible = false
        menu.findItem(R.id.menu_map_addtrack).isVisible = !stageOpen
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_map2_filter) {
            val qWfIntent = Intent(activity, ActivityFilter::class.java)
            startActivityForResult(qWfIntent, ACTIVITY_FILTER)
            return true
        } else if (item.itemId == R.id.menu_map_addtrack) {
            addNewTrack()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    internal fun addNewTrack() {
        val intentAddEdit = Intent(activity, ActivityTrackEdit::class.java)
        val bundle = Bundle()
        if (map != null) {
            bundle.putDouble(ActivityMapExtension.LAT, map!!.cameraPosition.target.latitude)
            bundle.putDouble(ActivityMapExtension.LON, map!!.cameraPosition.target.longitude)
            intentAddEdit.putExtras(bundle)
        }
        startActivity(intentAddEdit)
    }

    private fun zoomToInterestLocation() {
        if (interestLatLng != null && map != null) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(interestLatLng!!, MxPreferences.getInstance().mapZoom))
            Timber.d("animateCameraInterest $interestLatLng ${MxPreferences.getInstance().mapZoom}")
        }
    }

    private fun zoomToLocation(position: LatLng?) {
        if (position != null && map != null) {
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(position, MxPreferences.getInstance().mapZoom))
            Timber.d("animateCameraLocation $position ${MxPreferences.getInstance().mapZoom}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun zoomToMyLocation() {
        if (googleApiClient != null && googleApiClient!!.isConnected && map != null && isAdded) {
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                .addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        // Move the camera to the user's location if they are off-screen!
                        val latlong = LatLng(location.latitude, location.longitude)
                        map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, MxPreferences.getInstance().mapZoom))
                        Timber.d("animateCameraMy $latlong ${MxPreferences.getInstance().mapZoom}")
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(connectionHint: Bundle?) {
        startLocationUpdates()
        if (permissionHelper.hasLocationPermission() && isAdded) {
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                .addOnSuccessListener(requireActivity()) { location ->
                    if (location != null) {
                        headerView!!.setLocation(location)
                    }
                }
        }
        if (interestLatLng == null) {
            zoomToMyLocation()
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {}

    private fun updateClustering(enabled: Boolean) {
        if (map == null) {
            return
        }
        val clusteringSettings = ClusteringSettings()
        clusteringSettings.addMarkersDynamically(true)

        if (enabled) {
            clusteringSettings.clusterOptionsProvider(MapClusterOptionsProvider(resources))

            val clusterSize = CLUSTER_SIZES[CLUSTER_SIZE_NR]
            clusteringSettings.clusterSize(clusterSize)
        } else {
            clusteringSettings.enabled(false)
        }
        map!!.setClustering(clusteringSettings)
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        var query = SQuery.newQuery()
        return when (id) {
            LOADER_TRACKS -> {
                query = getTracksQuery(bundle, query)
                query.createSupportLoader(Tracksges.CONTENT_URI, null, Tracksges.TRACKNAME)
            }

            LOADER_STAGE -> StageHelperExtension.getStageQuery(0, true).createSupportLoader(Trackstage.CONTENT_URI, null, Trackstage.CREATED)
            LOADER_ROUTE -> SQuery.newQuery()
                .expr(Route.TRACK_CLIENT_ID, SQuery.Op.EQ, bundle!!.getLong(TRACK_CLIENT_ID))
                .createSupportLoader(Route.CONTENT_URI, null, Route.CREATED)

            LOADER_PROGRESS -> SQuery.newQuery().createSupportLoader(Importstatus.CONTENT_URI, null, Importstatus.CREATED + " desc")
            else -> throw RuntimeException("bad")
        }
    }

    private fun getTracksQuery(bundle: Bundle?, querySource: SQuery): SQuery {
        var query = querySource
        query = QueryHelper.buildTracksFilter(query, AbstractMxInfoDBOpenHelper.Sources.TRACKSGES)
        if (bundle != null && bundle.containsKey(SEARCH_TEXT)) {
            query =
                QueryHelper.buildUserTrackSearchFilter(query, bundle.getString(SEARCH_TEXT), false, AbstractMxInfoDBOpenHelper.Sources.TRACKS_GES_SUM)
            searchList!!.adapter = searchAdapter
        } else if (!inPlaceSearch) {
            searchList!!.adapter = null
        }

        return query
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_TRACKS -> {
                Timber.d("LOADER_TRACKS %s", cursor.count)
                if (searchView != null) {
                    if (searchView!!.isIconified) {
                        savePos = null
                    }
                }

                if (!MxPreferences.getInstance().mapCluster) {
                    map?.clearTrackMarkers()
                    map?.addFilteredMarkersAsync(cursor, false)
                } else {
                    map?.clearTrackMarkers()
                    map?.addFilteredMarkersAsync(cursor, false)
                }

                if (currId != 0L) {
                    val markerSource = map?.getTrackMarker(currId)
                    markerSource?.let {
                        val what = 1
                        val handler = Handler(Looper.getMainLooper()) { msg ->
                            if (msg.what == what) {
                                val marker = map?.getTrackMarker(currId)
                                slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
                                marker?.let {
                                    onMarkerSelect(it, currZoom)
                                }
                            }
                            true
                        }
                        handler.sendEmptyMessage(what)
                    }
                }

                searchAdapter!!.swapCursor(cursor)
                if (!inPlaceSearch) {
                    searchList!!.adapter = searchAdapter
                }
            }

            LOADER_STAGE -> {
                StageHelperExtension.clearStageMarkers()
                map?.let {
                    StageHelperExtension.addStageMarkers(it, cursor, true)
                }
            }

            LOADER_ROUTE -> {
                Timber.i("onLoadFinished Route:${cursor.count}")
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                if (polylineRoute != null) {
                    polylineRoute!!.remove()
                }
                if (cursor.count > 0) {
                    val routeRecord = RouteRecord.fromCursor(cursor)
                    val gson = Gson()
                    val routes = gson.fromJson(routeRecord.content, Routes::class.java)
                    var count = 0
                    routes.routes.forEach {
                        val decodedPath = PolyUtil.decode(it.overviewPolyline.points)
                        polylineRoute = map!!.addPolyline(
                            PolylineOptions().addAll(decodedPath)
                                .width(10f)
                                .color(
                                    if (count == 0)
                                        ContextCompat.getColor(requireActivity(), R.color.route_blue)
                                    else
                                        ContextCompat.getColor(requireActivity(), R.color.route_grey)
                                )
                        )
                        count++
                    }
                }
            }

            LOADER_PROGRESS -> {
                cursor.moveToFirst()
                if (cursor.count > 0 && cursor.getString(cursor.getColumnIndex(Importstatus.MSG)) != null &&
                    cursor.getString(cursor.getColumnIndex(Importstatus.MSG)) != ""
                ) {
                    lyProgress!!.visibility = View.VISIBLE
                    val txt = cursor.getString(cursor.getColumnIndex(Importstatus.MSG))
                    tvProgress!!.text = txt
                } else {
                    lyProgress!!.visibility = View.GONE
                    tvProgress!!.text = ""
                    Timber.i("loader hide")
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_TRACKS -> {
                map?.clearTrackMarkers()
                // clusterMarkerManager.removeAllMarker();
                searchAdapter!!.swapCursor(null)
            }

            LOADER_STAGE -> StageHelperExtension.clearStageMarkers()
            LOADER_ROUTE -> if (polylineRoute != null) {
                polylineRoute!!.remove()
            }

            LOADER_PROGRESS -> lyProgress!!.visibility = View.GONE
        }
    }

    override fun onConnectionSuspended(arg0: Int) {}

    override fun onCameraChange(cameraPosition: CameraPosition) {
        if (cameraPosition.target.latitude != 0.0 && cameraPosition.target.longitude != 0.0) {
            MxPreferences.getInstance().edit()
                .putMapZoom(cameraPosition.zoom)
                .putMapLatitude(cameraPosition.target.latitude.toFloat())
                .putMapLongitude(cameraPosition.target.longitude.toFloat())
                .commit()
        }
    }

    override fun onMapButtonStageClicked() {
        val show = binding.mapButtonsOvelay.toggleStageShow()
        if (show) {
            loaderManager.restartLoader(LOADER_STAGE, null, this)
        } else {
            loaderManager.destroyLoader(LOADER_STAGE)
        }
    }

    override fun onMapButtonTracksClicked() {
        val show = binding.mapButtonsOvelay.toggleTracksShow()
        if (show) {
            loaderManager.initLoader(LOADER_TRACKS, null, this)
        } else {
            loaderManager.destroyLoader(LOADER_TRACKS)
        }
    }

    override fun onMapButtonTrafficClicked() {
        val show = binding.mapButtonsOvelay.toggleTrafficShow()
        if (map != null) {
            map!!.isTrafficEnabled = show
            MxPreferences.getInstance().edit().putMapTraffic(show).commit()
        }
    }

    override fun onMapButtonTypeClicked() {
        val fragmentManager = fragmentManager
        val dialog = MapLayerDialog()
        val bundle = Bundle()
        dialog.arguments = bundle
        if (fragmentManager != null) {
            dialog.show(fragmentManager, "fragment_map_layer")
        }
    }

    override fun onMapButtonClusterClicked() {
        val show = binding.mapButtonsOvelay.toggleClusterShow()
        MxPreferences.getInstance().edit().putMapCluster(show).commit()
        updateClustering(show)
    }

    private fun zoomIn() {
        if (map != null && map!!.cameraPosition != null) {
            if (map!!.cameraPosition.zoom < 20) {
                //                CameraUpdate newPos = CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target,
                //                        map.getCameraPosition().zoom + 2);
                //                map.animateCamera(newPos);
                map!!.animateCamera(CameraUpdateFactory.zoomIn())
                Timber.d("animateCameraZoomIn %s", MxPreferences.getInstance().mapZoom)
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        setSearchListVisibility(View.GONE)
        onMarkerSelect(marker, 0f)
        return false
    }

    private fun onMarkerSelect(marker: Marker, zoom: Float) {
        if (!isAdded) {
            return
        }

        if (lastInterstitial < System.currentTimeMillis() - INTERSTITIAL_WAIT_MS) {
            lastInterstitial = System.currentTimeMillis()
            val runnableErrorHide = {
                if (activity != null) {
                    addMobHelper.showInterstitial(requireActivity())
                }
            }
            interstitialHandler.postDelayed(runnableErrorHide, 5000)

        }

        marker.showInfoWindow() // bring to front
        hideKeyBoard()
        animateCameraToMarker(marker, zoom)
        setSliderVisibility(View.VISIBLE)
        var stageID: Long? = null
        var trackID: Long? = null
        var mxPlace: MxPlace? = null
        if (marker.getData<Any>() is TrackstageRecord) {
            stageID = (marker.getData<Any>() as TrackstageRecord).id
        } else if (marker.getData<Any>() is Long) {
            trackID = marker.getMarkerId()
        } else if (marker.getData<Any>() is MxPlace) {
            mxPlace = marker.getData<MxPlace>()
        }
        if (trackID != null) {
            openTrackInSlider(trackID)
        } else if (stageID != null) {
            openStageInSlider(stageID)
        } else if (mxPlace != null) {
            openPlaceInSlider(mxPlace)
        } else {
            zoomIn()
        }
    }

    private fun hideKeyBoard() {
        if (searchView != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            imm.hideSoftInputFromWindow(searchView!!.windowToken, 0)
            isKeyboardActive = false
        }
    }

    private fun showKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        isKeyboardActive = true
    }

    private fun animateCameraToMarker(marker: Marker, zoomAnimate: Float) {
        var zoom = zoomAnimate
        if (map != null && map!!.cameraPosition != null) {
            if (zoom == 0f) {
                zoom = map!!.cameraPosition.zoom
            }
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, zoom)
            map!!.animateCamera(cameraUpdate)
            Timber.d("animateCameraMarker ${marker.position} $zoom")

            //            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //            builder.include(marker.getPosition());
            //            LatLngBounds bounds = builder.build();
            //            int padding = 80; // offset from edges of the map in pixels
            //            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //            map.animateCamera(cu);
        }
    }

    override fun onHeaderLayoutUpdated(height: Int) {
        slidingDrawer!!.panelHeight = height
        Timber.d("setHeaderPanelHeight:%s", height)
        //to calibrate the collapsed height
        if (slidingDrawer!!.getPanelState() == PanelState.COLLAPSED || slidingDrawer!!.getPanelState() == PanelState.HIDDEN) {
            slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
        }
    }

    override fun onHeaderClicked(view: View, currentPoi: TracksGesSumRecord?) {
        if (slidingDrawer!!.getPanelState() == PanelState.COLLAPSED) {
            slidingDrawer!!.setPanelState(PanelState.ANCHORED)
            if (currentPoi != null) {
                val poiLatLng = LatLng(SecHelper.entcryptXtude(currentPoi.latitude), SecHelper.entcryptXtude(currentPoi.longitude))
                zoomToLocation(poiLatLng)
            }
        } else if (slidingDrawer!!.getPanelState() == PanelState.ANCHORED) {
            slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
        } else if (slidingDrawer!!.getPanelState() == PanelState.EXPANDED) {
            slidingDrawer!!.setPanelState(PanelState.COLLAPSED)
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
                    map!!.isMyLocationEnabled = true
                    LocationJobService.scheduleJob(requireActivity())
                    setUpLocationClientIfNeeded()
                }
            }
        }
    }

    companion object {

        private val SEARCH_TEXT = "search_text"
        private val MIN_TEXT_SEARCH_LENGTH = 2
        private val INTERSTITIAL_WAIT_MS: Long = 90000
        private val TOLERANCE = 60
        private val TRACK_CLIENT_ID = "TRACK_CLIENT_ID"
        private val CURR_ZOOM = "CURR_ZOOM"
        private val ACTIVITY_FILTER = 0
        private val LOADER_TRACKS = 0
        private val LOADER_STAGE = 1
        private val LOADER_ROUTE = 2
        private val LOADER_PROGRESS = 3
        private val CLUSTER_SIZES = doubleArrayOf(180.0, 160.0, 144.0, 120.0, 96.0)
        private val CLUSTER_SIZE_NR = 4
    }
}
