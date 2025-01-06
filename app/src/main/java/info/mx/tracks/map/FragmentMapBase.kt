package info.mx.tracks.map

import android.os.Bundle
import android.view.View
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.SupportMapFragment
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import timber.log.Timber

abstract class FragmentMapBase : FragmentBase() {

    private var mapFragment: SupportMapFragment? = null
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
        if (map == null) {
            MapIdlingResource.increment()
            MapIdlingResource.increment()
            mapFragment?.getExtendedMapAsync { googleMap ->
                map = googleMap
                MapIdlingResource.decrement()
                setUpMap()
                map!!.setOnMapLoadedCallback { MapIdlingResource.decrement() }
            }
        }
    }

    protected abstract fun setUpMap()
}
