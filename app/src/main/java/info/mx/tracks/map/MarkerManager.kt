package info.mx.tracks.map

import android.content.Context
import android.database.Cursor
import com.androidmapsextensions.ClusterGroup
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.Marker
import com.androidmapsextensions.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import info.mx.tracks.R
import info.mx.tracks.common.BitmapHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Add filtered markers to the map asynchronously using coroutines.
 * This function launches a coroutine that processes the markers.
 *
 * @param tracksCursor Cursor containing track data
 * @param animateTo Whether to animate the camera to show all markers
 * @param scope Optional CoroutineScope to use. If not provided, uses a default IO scope.
 */
fun GoogleMap.addFilteredMarkersAsync(
    tracksCursor: Cursor,
    animateTo: Boolean,
    scope: CoroutineScope? = null
) {
    if (tracksCursor.count > 0) {
        val markerTask = AsyncTaskAddMarker(this, animateTo, tracksCursor)
        // Launch in the provided scope or create a default one
        val coroutineScope = scope ?: CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            markerTask.execute()
        }
    }
}

fun Context.getMarkerOption(
    id: Long,
    lat: Double,
    lon: Double,
    iconRes: Int,
    trackAccess: String?
): MarkerOptions {
    val bitmapDescriptor = BitmapHelper.getBitmapDescriptor(this, iconRes, trackAccess)
    return MarkerOptions()
        .data(id)
        .position(LatLng(lat, lon))
        .icon(bitmapDescriptor)
}

fun String.getTrackPinId(): Int {
    return when (this) {
        "M" -> R.drawable.pin_member
        "R" -> R.drawable.pin_race
        "D" -> R.drawable.pin_shop2x
        else -> R.drawable.pin_normal
    }
}

fun Marker.getMarkerId(): Long = getData()

fun GoogleMap.clearTrackMarkers() {
    for (marker in markers) {
        if (marker.getData<Any>() is Long) {
            marker.remove()
        }
    }
}

fun GoogleMap.getTrackMarker(id: Long): Marker? {
    var result: Marker? = null
    for (marker in markers) {
        if (marker.getData<Any>() == id) {
            result = marker
            break
        }
    }
    return result
}

fun MxPlace.getMarkerOption(): MarkerOptions {
    return MarkerOptions()
        .data(this)
        .clusterGroup(ClusterGroup.NOT_CLUSTERED)
        .position(this.latLng!!)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_poi_normal))
}

fun GoogleMap.removeAllPlacesMarker() {
    for (marker in markers) {
        if (marker.getData<Any>() is MxPlace) {
            marker.remove()
        }
    }
}
