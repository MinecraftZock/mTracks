package info.mx.tracks.map

import android.content.Context
import android.database.Cursor
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TrackMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

/**
 * Coroutine-based marker loading and drawing for Google Maps.
 * Replaces the deprecated AsyncTask with modern Kotlin coroutines.
 */
class AsyncTaskAddMarker internal constructor(
    private val mGMap: GoogleMap,
    private var animateTo: Boolean,
    private val cursor: Cursor
) : KoinComponent {

    private val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    private val tracks: MutableList<TrackMap> = ArrayList()

    val context: Context by inject()

    /**
     * Execute the marker loading process using coroutines.
     * Call this from a coroutine scope (e.g., lifecycleScope.launch { ... })
     */
    suspend fun execute() {
        val startTime = System.currentTimeMillis()

        // Prepare tracks on main thread (cursor access)
        prepareTracks(startTime)

        // Process markers and draw them
        val resultBuilder = processMarkers()

        // Finalize on main thread
        finalize(resultBuilder, startTime)
    }

    private fun prepareTracks(startTime: Long) {
        if (cursor.count > 0) {
            val posAccess = cursor.getColumnIndex(MxInfoDBContract.Tracksges.TRACKACCESS)
            val posStatus = cursor.getColumnIndex(MxInfoDBContract.Tracksges.TRACKSTATUS)
            val posLat = cursor.getColumnIndex(MxInfoDBContract.Tracksges.LATITUDE)
            val posLon = cursor.getColumnIndex(MxInfoDBContract.Tracksges.LONGITUDE)
            val posId = cursor.getColumnIndex(MxInfoDBContract.Tracksges._ID)

            try {
                cursor.moveToPosition(-1)
                while (cursor.moveToNext()) {
                    var lat = 0.0
                    var lon = 0.0
                    if (cursor.getString(posLat) != "") {
                        lat = cursor.getDouble(posLat)
                    }
                    if (cursor.getString(posLon) != "") {
                        lon = cursor.getDouble(posLon)
                    }
                    val track = TrackMap(
                        cursor.getLong(posId),
                        lat,
                        lon,
                        cursor.getString(posAccess),
                        cursor.getString(posStatus)
                    )
                    tracks.add(track)
                }
                cursor.moveToFirst()
            } catch (e: Exception) {
                Timber.w(e)
            }

            Timber.d("PreExecute time:%s", System.currentTimeMillis() - startTime)
        }
    }

    private suspend fun processMarkers(): LatLngBounds.Builder = withContext(Dispatchers.Default) {
        var markerIncluded = false

        tracks.forEach { track ->
            val markerId = track.access.getTrackPinId()
            val markerOption = context.getMarkerOption(
                track._id,
                track.latDecrypt,
                track.lonDecrypt,
                markerId,
                track.status
            )

            // Draw marker on main thread
            withContext(Dispatchers.Main) {
                drawMarker(mGMap, markerOption)
            }

            val position = LatLng(track.latDecrypt, track.lonDecrypt)
            builder.include(position)
            markerIncluded = true
        }

        // animate only with given poi
        animateTo = animateTo && markerIncluded
        builder
    }

    private fun finalize(resultBuilder: LatLngBounds.Builder, startTime: Long) {
        val milliSec = System.currentTimeMillis() - startTime
        Timber.i("%s ms add marker:", milliSec)

        if (animateTo) {
            val bounds = resultBuilder.build()
            val padding = 80 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mGMap.animateCamera(cu)
            Timber.d("animateCameraPost $bounds $padding")
        }
    }

    private fun drawMarker(googleMap: GoogleMap, markerOption: MarkerOptions) {
        googleMap.addMarker(markerOption)
    }
}
