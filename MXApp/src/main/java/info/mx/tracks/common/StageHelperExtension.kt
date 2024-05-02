package info.mx.tracks.common

import android.database.Cursor
import android.database.StaleDataException
import com.androidmapsextensions.ClusterGroup
import com.androidmapsextensions.GoogleMap
import com.androidmapsextensions.Marker
import com.androidmapsextensions.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.R
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.sqlite.MxInfoDBContract.Tracks
import info.mx.tracks.sqlite.MxInfoDBContract.Trackstage
import info.mx.tracks.sqlite.TrackstageRecord
import timber.log.Timber

object StageHelperExtension {

    private val stageHashMap = HashMap<Marker?, Long>()

    fun addStageMarkers(mMap: GoogleMap, cursor: Cursor, showAllStage: Boolean) {
        clearStageMarkers()
        var showBounds = false
        val builder = LatLngBounds.Builder()
        while (cursor.moveToNext()) {
            val stage = TrackstageRecord.fromCursor(cursor)
            val stageMarker = addMarkerStage(mMap, stage, cursor.getStringVal())
            stageHashMap[stageMarker] = stage.id
            builder.include(stageMarker!!.position)
            showBounds = true
        }
        if (showAllStage && showBounds) {
            val bounds = builder.build()
            val padding = 10 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.animateCamera(cu)
            Timber.d("animateCameraStage %s", padding)
        }
    }

    fun getStageQuery(trackRestID: Long, withLatLngChange: Boolean): SQuery {
        val query = SQuery.newQuery()
            .expr(Trackstage.TRACK_REST_ID, SQuery.Op.EQ, trackRestID)
        if (withLatLngChange) {
            val queryLatLng = SQuery.newQuery()
                .expr(Trackstage.LATITUDE, SQuery.Op.NEQ, 0)
                .and()
                .expr(Trackstage.ANDROIDID, SQuery.Op.NEQ, "confirm")
            query.or().expr(queryLatLng)
        }
        val mainQuery = SQuery.newQuery().expr(query)
        if (MxPreferences.getInstance().showOnlyNewTrackStage) {
            val approved = SQuery.newQuery()
                .expr(Trackstage.APPROVED, SQuery.Op.EQ, 0)
                .or()
                .append(Trackstage.APPROVED + " is null")
            mainQuery.expr(approved)
        }
        return mainQuery
    }

    fun clearStageMarkers() {
        for ((key) in stageHashMap) {
            key!!.remove()
        }
        stageHashMap.clear()
    }

    private fun addMarkerStage(
        mMap: GoogleMap?,
        stage: TrackstageRecord,
        stageDetail: CharSequence
    ): Marker? {
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
                    .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                    .data(stage)
                    .position(latlng)
                    .draggable(true)
                    .title(stage.trackname)
                    .snippet(stageDetail.toString())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_stage))
            )
        }
        return result
    }

}

fun Cursor.getStageValues(): String {
    var res = ""
    try {
        for (i in 0 until this.columnCount) {
            if (this.getColumnName(i) != Trackstage._ID &&
                this.getColumnName(i) != Trackstage.REST_ID &&
                this.getColumnName(i) != Trackstage.TRACK_REST_ID &&
                !this.getColumnName(i).startsWith("Ins") &&
                this.getColumnName(i) != Trackstage.ANDROIDID &&
                this.getColumnName(i) != Trackstage.CREATED &&
                this.getColumnName(i) != Trackstage.APPROVED
            ) {
                if (this.getString(i) != null && this.getString(i) != "0" && this.getString(i) != "0.0" && this.getString(i) != "") {
                    res += "\n${this.getColumnName(i)}=${this.getString(i).trimIndent()}"
                }
            }
        }
    } catch (e: StaleDataException) {
        res = "Cursor is closed"
    }
    return res.trim { it <= ' ' }.replace("\n".toRegex(), "<br />")
}

fun Cursor.getStringVal(): String {
    var res = ""
    val proj: Array<String>? = null
    val trackCur: Cursor
    val trackRestId = this.getColumnIndex(Trackstage.TRACK_REST_ID)
    trackCur = SQuery.newQuery().expr(Tracks.REST_ID, SQuery.Op.EQ, this.getString(trackRestId))
        .select(Tracks.CONTENT_URI, proj)
    trackCur.moveToFirst()
    if (trackCur.count == 0) {
        if (this.getLong(trackRestId) != 0L) {
            Timber.e("Track not found trackRestId:%s", this.getString(trackRestId))
        }
        return res
    }
    for (i in 0 until this.columnCount) {
        if (this.getColumnName(i) != Trackstage._ID &&
            this.getColumnName(i) != Trackstage.REST_ID &&
            this.getColumnName(i) != Trackstage.TRACK_REST_ID &&
            !this.getColumnName(i).startsWith("Ins") &&
            this.getColumnName(i) != Trackstage.ANDROIDID &&
            this.getColumnName(i) != Trackstage.CREATED &&
            this.getColumnName(i) != Trackstage.APPROVED
        ) {
            if (this.getString(i) != null && this.getString(i) != "0" && this.getString(i) != "0.0"
                && this.getString(i) != ""
            ) {
                val colIndex = trackCur.getColumnIndex(this.getColumnName(i))
                var colorStrPre = ""
                if (colIndex == -1) {
                    colorStrPre = "<font color=\"blue\">"
                    Timber.w("missing field:%s", this.getColumnName(i))
                } else if (trackCur.getString(colIndex) == null || trackCur.getString(colIndex) == this.getString(i)) {
                    // nothing
                } else {
                    colorStrPre = "<font color=\"red\">"
                }
                res += "${this.getColumnName(i)}=$colorStrPre${this.getString(i)}${if (colorStrPre == "") "" else "</font>"}".trimIndent()
            }
        }
    }
    return res.trim { it <= ' ' }.replace("\n".toRegex(), "<br />")
}
