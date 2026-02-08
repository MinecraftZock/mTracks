package info.mx.tracks.common

import android.database.Cursor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.robotoworks.mechanoid.db.SQuery
import info.mx.core_generated.prefs.MxPreferences.Companion.instance
import info.mx.core_generated.sqlite.MxInfoDBContract
import info.mx.core_generated.sqlite.MxInfoDBContract.Trackstage
import info.mx.core_generated.sqlite.TrackstageRecord
import info.mx.tracks.R
import timber.log.Timber

object StageHelper {
    private val stageHashMap = HashMap<Marker?, Long?>()

    fun addStageMarkers(mMap: GoogleMap?, cursor: Cursor, showAllStage: Boolean) {
        clearStageMarkers()
        var showBounds = false
        val builder = LatLngBounds.Builder()
        while (cursor.moveToNext()) {
            val stage = TrackstageRecord.fromCursor(cursor)
            if (stage.latitude != 0.0) {
                val stageMarker = addMarkerStage(mMap, stage, getStringVal(cursor))
                stageHashMap[stageMarker] = stage.id
                builder.include(stageMarker.position)
                showBounds = true
            }
        }
        if (showAllStage && showBounds) {
            val bounds = builder.build()
            val padding = 10 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap?.animateCamera(cu)
        }
    }

    fun getStageQuery(trackRestID: Long): SQuery {
        val approved = SQuery.newQuery()
            .expr(Trackstage.APPROVED, SQuery.Op.EQ, 0)
            .or()
            .append(Trackstage.APPROVED + " is null")
        val query = SQuery.newQuery()
            .expr(Trackstage.TRACK_REST_ID, SQuery.Op.EQ, trackRestID)
        if (instance.showOnlyNewTrackStage) {
            query.expr(approved)
        }
        return query
    }

    private fun clearStageMarkers() {
        for (entry in stageHashMap.entries) {
            entry.key!!.remove()
        }
        stageHashMap.clear()
    }

    fun getId(marker: Marker?): Long? {
        return stageHashMap.get(marker)
    }

    private fun addMarkerStage(
        mMap: GoogleMap?,
        stage: TrackstageRecord,
        stageDetail: CharSequence
    ): Marker {
        var result: Marker? = null
        if (mMap != null) {
            var titel = ""
            if (stage.approved == -1L) {
                titel = "--"
            } else if (stage.approved == 1L) {
                titel = "++"
            }
            val latlng = LatLng(stage.latitude, stage.longitude)
            result = mMap.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .title(stage.trackname)
                    .snippet(stageDetail.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stage))
            )
        }
        return result!!
    }

    private fun getStringVal(cursor: Cursor): String {
        var res = ""

        val proj: Array<String?>? = null
        val trackCur: Cursor
        val trackRestId = cursor.getColumnIndex(Trackstage.TRACK_REST_ID)
        trackCur = SQuery.newQuery()
            .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, cursor.getString(trackRestId))
            .select(MxInfoDBContract.Tracks.CONTENT_URI, proj)
        trackCur.moveToFirst()
        if (trackCur.count == 0) {
            if (cursor.getLong(trackRestId) != 0L) {
                Timber.e("Track not found trackRestId:%s", cursor.getString(trackRestId))
            }
            return res
        }
        for (i in 0..<cursor.columnCount) {
            if ((cursor.getColumnName(i) != Trackstage._ID) &&
                (cursor.getColumnName(i) != Trackstage.REST_ID) &&
                (cursor.getColumnName(i) != Trackstage.TRACK_REST_ID) &&
                !cursor.getColumnName(i).startsWith("Ins") &&
                (cursor.getColumnName(i) != Trackstage.ANDROIDID) &&
                (cursor.getColumnName(i) != Trackstage.CREATED) &&
                (cursor.getColumnName(i) != Trackstage.APPROVED)
            ) {
                if (cursor.getString(i) != null &&
                    (cursor.getString(i) != "0") &&
                    (cursor.getString(i) != "0.0") &&
                    !cursor.getString(i).isEmpty()
                ) {
                    val colIndex = trackCur.getColumnIndex(cursor.getColumnName(i))
                    var colorStrPre = ""
                    if (colIndex == -1) {
                        colorStrPre = "<font color=\"blue\">"
                        Timber.w("missing field:%s", cursor.getColumnName(i))
                    } else if (trackCur.getString(colIndex) == null ||
                        trackCur.getString(colIndex) == cursor.getString(i)
                    ) {
                        // nothing
                    } else {
                        colorStrPre = "<font color=\"red\">"
                    }
                    res += "\n" + cursor.getColumnName(i) + "=" + colorStrPre + cursor.getString(i) + (if (colorStrPre.isEmpty()) "" else "</font>")
                }
            }
        }
        return res.trim { it <= ' ' }.replace("\n".toRegex(), "<br />")
    }
}
