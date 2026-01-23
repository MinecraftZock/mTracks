package info.mx.tracks.map

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TrackMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*

/**
 * You can do something like this. Load the data you need in doInBackground and draw the markers in onProgressUpdate. To call onProgressUpdate just
 * do a publishProgress() from doInBackground. That way you can update the UI from within the ASyncTask.
 */
class AsyncTaskAddMarker internal constructor(private val mGMap: GoogleMap, private var animateTo: Boolean, private val cursor: Cursor) :
    AsyncTask<Void, MarkerOptions, LatLngBounds.Builder>(), KoinComponent {
    private var startTime: Long = 0
    private val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    private val tracks: MutableList<TrackMap>

    val context: Context by inject()

    init {
        tracks = ArrayList()
    }

    override fun onPreExecute() {
        if (cursor.count > 0) {
            startTime = System.currentTimeMillis()
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
                    val track = TrackMap(cursor.getLong(posId), lat, lon, cursor.getString(posAccess), cursor.getString(posStatus))
                    tracks.add(track)
                }
                cursor.moveToFirst()
            } catch (e: Exception) {
                Timber.w(e)
            }

            Timber.d("PreExecute time:%s", System.currentTimeMillis() - startTime)
        }
    }

    override fun doInBackground(vararg nothing: Void): LatLngBounds.Builder {
        var markerIncluded = false
        var markerId: Int

        tracks.forEach {
            markerId = it.access.getTrackPinId()
            val markerOption = context.getMarkerOption(it._id, it.latDecrypt, it.lonDecrypt, markerId, it.status)

            publishProgress(markerOption)

            val position = LatLng(it.latDecrypt, it.lonDecrypt)
            builder.include(position)
            markerIncluded = true
        }
        // animate only with given poi
        animateTo = animateTo && markerIncluded
        return builder
    }

    override fun onPostExecute(resultBuilder: LatLngBounds.Builder) {
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

    override fun onProgressUpdate(vararg markerOption: MarkerOptions) {
        drawMarker(this.mGMap, markerOption[0])
    }
}
