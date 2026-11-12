package info.mx.tracks.map

import android.os.Bundle
import android.view.View
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.OnMapReadyCallback
import com.androidmapsextensions.SupportMapFragment
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import timber.log.Timber

abstract class FragmentMapBase : FragmentBase() {

    private var mapFragment: SupportMapFragment? = null
    private var pendingMapLoad: Any? = null
    private var mapLoaded = false
    protected var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment?

        if (mapFragment == null) {
            Timber.d("using getFragmentManager")
            mapFragment = parentFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment?
        }

    }

    override fun onResume() {
        super.onResume()
        setUpMapIfNeeded()
    }

    private fun setUpMapIfNeeded() {
        if (map != null || pendingMapLoad != null) {
            return
        }
        val fragment = checkNotNull(mapFragment) { "Cannot load map without a SupportMapFragment" }
        val load = Any()
        pendingMapLoad = load
        MapIdlingResource.increment()
        fragment.getExtendedMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                if (pendingMapLoad !== load) {
                    Timber.d("Ignoring map readiness callback for a destroyed view")
                    return
                }
                map = googleMap
                if (!MapRenderingMode.screenshotMode) {
                    googleMap.setOnMapLoadedCallback(object : GoogleMap.OnMapLoadedCallback {
                        override fun onMapLoaded() {
                            if (pendingMapLoad !== load) {
                                return
                            }
                            mapLoaded = true
                            this@FragmentMapBase.onMapLoaded()
                        }
                    })
                }
                setUpMap()
                if (MapRenderingMode.screenshotMode) {
                    mapLoaded = true
                    this@FragmentMapBase.onMapLoaded()
                }
            }
        })
    }

    private fun finishMapLoad() {
        if (pendingMapLoad != null) {
            pendingMapLoad = null
            MapIdlingResource.decrement()
        }
    }

    protected fun finishMapContentLoad() {
        if (mapLoaded) {
            finishMapLoad()
        }
    }

    override fun onDestroyView() {
        if (pendingMapLoad != null) {
            Timber.w("Cancelling unfinished map load because its view was destroyed")
            finishMapLoad()
        }
        map = null
        mapFragment = null
        mapLoaded = false
        super.onDestroyView()
    }

    protected abstract fun onMapLoaded()

    protected abstract fun setUpMap()
}
