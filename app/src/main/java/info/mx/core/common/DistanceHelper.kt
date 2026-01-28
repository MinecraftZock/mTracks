package info.mx.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.DateHelper.getTimeStrFromMinutesDay
import info.mx.commonlib.LocationHelper.getFormatDistance
import info.mx.core.rest.google.Routes
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracksges
import info.mx.core_generated.sqlite.RouteRecord
import info.mx.core_generated.sqlite.TracksGesSumRecord
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TracksgesRecord
import info.mx.tracks.common.SecHelper
import timber.log.Timber
import java.text.DecimalFormat
import kotlin.math.roundToInt

object DistanceHelper {
    private const val MAX_DISTANCE = 99999999
    private const val MAX_SECONDS = 24 * 3600 * 4

    @JvmStatic
    fun calcDistance2TracksCrypt(
        lat: Double,
        lon: Double,
        updateGUI: Boolean,
        country: String?,
        context: Context
    ) {
        // special Uri to prevent GUI update during BulkInsert, GUI will be updated during distance calculation later
//        final Uri uri = Tracks.CONTENT_URI
        //                .buildUpon()
        //                .appendQueryParameter(MechanoidContentProvider.PARAM_NOTIFY, "false")
        //                .build();
        val df = DecimalFormat("0.00")
        val currentPos = Location("center")
        currentPos.latitude = lat
        currentPos.longitude = lon
        // // #1 calculate distance not trigonometric, because SQlite hasn't such functions
        if (lat != 0.0) {
            val start = System.currentTimeMillis()
            val projection = arrayOf(Tracks._ID, Tracks.LATITUDE, Tracks.LONGITUDE)
            var zlr = 0
            // final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            val cursor = SQuery.newQuery()
                .expr(Tracks.COUNTRY, SQuery.Op.EQ, country)
                .select(Tracks.CONTENT_URI, projection)
            cursor.moveToPosition(-1)
            while (cursor.moveToNext()) {
                zlr++
                val dest = Location("dest")
                dest.latitude = SecHelper.entcryptXtude(cursor.getDouble(1))
                dest.longitude = SecHelper.entcryptXtude(cursor.getDouble(2))
                val distance = currentPos.distanceTo(dest).roundToInt()
                Tracks.newBuilder()
                    .setDistance2location(distance.toLong())
                    .update(cursor.getLong(0), false)
            }
            cursor.close()
            val seconds = (System.currentTimeMillis() - start) / 1000.0
            Timber.d("calcDistanceByCountry $zlr records set distance2current: LOC:${df.format(seconds)}s")

            // #2 mark content provider to update gui
            if (updateGUI) {
                context.contentResolver.notifyChange(Tracksges.CONTENT_URI, null)
                context.contentResolver.notifyChange(Tracks.CONTENT_URI, null)
            }
        }
    }

    // private static void doRecalcDistance(double lat, double lon) {
    // String wann = sdf.format(new Date(System.currentTimeMillis()));
    // long start = System.currentTimeMillis();
    // String y = doRecalc(MxInfoDBOpenHelper.DBPATH, lat, lon);
    // start = System.currentTimeMillis() - start;
    // if (MxApplication.isAdmin) {
    // wann = sdf.format(new Date(System.currentTimeMillis()));
    // LoggingHelper.setMessage(wann + " " + start + "ms " + y);
    // }
    // };
    //
    // private native static String doRecalc(String dbpath, double lat, double lon);
    @SuppressLint("SetTextI18n")
    @JvmStatic
    fun setDistanceString(routeRecord: RouteRecord, textView: TextView) {
        val gson = Gson()
        val routes = gson.fromJson(routeRecord.content, Routes::class.java)
        var seconds2Track = MAX_SECONDS
        for (route in routes.routes) {
            for (leg in route.legs) {
                if (seconds2Track > leg.duration.value) {
                    seconds2Track = leg.duration.value
                }
            }
        }
        var distance2Track = MAX_DISTANCE
        for (route in routes.routes) {
            for (leg in route.legs) {
                if (distance2Track > leg.distance.value) {
                    distance2Track = leg.distance.value
                }
            }
        }
        if (distance2Track < MAX_DISTANCE) {
            textView.alpha = 1f
            textView.text = getFormatDistance(MxPreferences.getInstance().unitsKm, distance2Track)
            Timber.d("Route:%s", textView.text.toString())
        }
        if (seconds2Track < MAX_SECONDS) {
            textView.alpha = 1f
            textView.text = getTimeStrFromMinutesDay((seconds2Track / 60).toFloat().roundToInt()) + "h  " + textView.text
            Timber.d("Route:%s", textView.text.toString())
        }
    }

    fun checkDistance4View(poiRecord: TracksgesRecord, viewDistance: TextView, myLoc: Location?) {
        val distanceOld = poiRecord.distance2location.toInt()
        val prefs = MxPreferences.getInstance()
        val distNew: Int
        if (myLoc != null) {
            viewDistance.visibility = View.VISIBLE
            val trackLoc = Location("track")
            trackLoc.latitude = SecHelper.entcryptXtude(poiRecord.latitude)
            trackLoc.longitude = SecHelper.entcryptXtude(poiRecord.longitude)
            distNew = myLoc.distanceTo(trackLoc).roundToInt()
            if (getFormatDistance(prefs.unitsKm, distNew) != getFormatDistance(
                    prefs.unitsKm,
                    distanceOld
                )
            ) {
                val track = TracksRecord.get(poiRecord.id)
                if (track != null) {
                    Timber.d(
                        "storeDistance2DB ${getFormatDistance(prefs.unitsKm, distanceOld)} -> " +
                                "${getFormatDistance(prefs.unitsKm, distNew)} ${poiRecord.trackname}"
                    )
                    track.distance2location = distNew.toLong()
                    track.save(false)
                }
            }
            viewDistance.text = getFormatDistance(prefs.unitsKm, distNew)
            if (distanceOld != 0) {
                viewDistance.alpha = 0.5f
            } else {
                viewDistance.alpha = 1f
            }
            viewDistance.visibility = View.VISIBLE
        } else {
            viewDistance.visibility = View.INVISIBLE
        }
    }

    @JvmStatic
    fun checkDistance4View(
        poiRecord: TracksGesSumRecord,
        viewDistance: TextView,
        myLoc: Location?
    ) {
        val distanceOld = poiRecord.distance2location.toInt()
        val prefs = MxPreferences.getInstance()
        val distNew: Int
        if (myLoc != null) {
            viewDistance.visibility = View.VISIBLE
            val trackLoc = Location("track")
            trackLoc.latitude = SecHelper.entcryptXtude(poiRecord.latitude)
            trackLoc.longitude = SecHelper.entcryptXtude(poiRecord.longitude)
            distNew = myLoc.distanceTo(trackLoc).roundToInt()
            if (getFormatDistance(prefs.unitsKm, distNew) != getFormatDistance(
                    prefs.unitsKm,
                    distanceOld
                )
            ) {
                val track = TracksRecord.get(poiRecord.id)
                if (track != null) {
                    Timber.d(
                        "${
                            getFormatDistance(prefs.unitsKm, distanceOld)
                        } -> ${getFormatDistance(prefs.unitsKm, distNew)} ${poiRecord.trackname}"
                    )
                    track.distance2location = distNew.toLong()
                    track.save(false)
                }
            }
            viewDistance.text =
                getFormatDistance(prefs.unitsKm, distNew)
            if (distanceOld != 0) {
                viewDistance.alpha = 0.5f
            } else {
                viewDistance.alpha = 1f
            }
            viewDistance.visibility = View.VISIBLE
        } else {
            viewDistance.visibility = View.INVISIBLE
        }
    }
}
