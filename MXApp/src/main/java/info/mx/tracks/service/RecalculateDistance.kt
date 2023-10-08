package info.mx.tracks.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.DateHelper
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.common.*
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.CapturedLatLng
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.MxMemDatabase
import info.mx.tracks.room.TracksDistance
import info.mx.tracks.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.tracks.sqlite.CountryRecord
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.MxInfoDBContract.Tracksges
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ActivityTrackDetail
import info.mx.tracks.util.LocationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class RecalculateDistance(private val context: Context) : KoinComponent {

    val mxDatabase: MxDatabase by inject()

    private val mxMemDatabase: MxMemDatabase by inject()

    private val notificationID: Int
        get() = NOTIFICATION_ID

    @SuppressLint("CheckResult")
    fun recalculateTracks(location: Location, source: String) {
        mxDatabase.capturedLatLngDao().lastNonIgnoredLocation
            .subscribe(
                { (_, lat, lon) ->
                    val lastKnown = Location("lastKnown")
                    lastKnown.latitude = lat
                    lastKnown.longitude = lon
                    proceedWithDistance(location, lastKnown.distanceTo(location).roundToInt(), source)
                },
                { t -> Timber.e(t) },
                { proceedWithDistance(location, Integer.MAX_VALUE, source) }
            )
    }

    private fun proceedWithDistance(location: Location, distance: Int, source: String) {
        val start = System.currentTimeMillis()
        var extra = ""
        var trackName = ""

        val capturedLatLng = CapturedLatLng()
        capturedLatLng.lat = location.latitude
        capturedLatLng.lon = location.longitude
        capturedLatLng.distanceToLast = distance
        capturedLatLng.time = System.currentTimeMillis()

        if (distance > DISTANCE_MIN_TO_RECALC) {

            val records = calculateDistanceOnTracks(mxMemDatabase, location)
            if (records.isNotEmpty()) {
                val trackLoc = Location("track")
                trackLoc.latitude = records[0].lat
                trackLoc.longitude = records[0].lon
                capturedLatLng.distToNearest = trackLoc.distanceTo(location).roundToInt()
                extra = updateNotification(trackLoc.distanceTo(location).roundToInt(), records[0].id!!)
                if (MxCoreApplication.isAdmin) {
                    val trackRecord = TracksRecord.get(records[0].id!!)
                    if (trackRecord != null) {
                        trackName = trackRecord.trackname
                    }
                }
            }
        } else {
            updateNotification4Admin(distance.toFloat().roundToInt(), ACTION_IGNORED)
            capturedLatLng.action = ACTION_IGNORED
            extra = " min distance"
        }

        if (distance == Integer.MAX_VALUE) {
            capturedLatLng.distanceToLast = 0
            capturedLatLng.action = ACTION_FIRST
        }

        capturedLatLng.extra = "$source $extra"
        capturedLatLng.trackname = trackName
        mxDatabase.capturedLatLngDao().insertAll(capturedLatLng)

        //reset countries to show
        if (!MxPreferences.getInstance().firstTimeLocation) {
            if (SQuery.newQuery().count(MxInfoDBContract.Country.CONTENT_URI) > 2) {
                if (LocationHelper.isAmerica(location)) {
                    hideEurope(context)
                } else if (isEurope(location)) {
                    LocationHelper.hideAmerica(context)
                } else {
                    LocationHelper.hideAmerica(context)
                }
                QueryHelper.resetFilter()
                MxPreferences.getInstance().edit().putFirstTimeLocation(true).commit()
            }
        }

        Timber.d("time  : %s", (System.currentTimeMillis() - start) / 1000.0)

        val sendNewDistance = Intent(DISTANCE_NEW)
        context.sendBroadcast(sendNewDistance)
    }

    private fun hideEurope(context: Context) {
        val countries = SQuery.newQuery().select<CountryRecord>(MxInfoDBContract.Country.CONTENT_URI)
        for (country in countries) {
            val latitude = CountryTools.getLatitude(country.country)
            val longitude = CountryTools.getLongitude(country.country)
            val countryLocation = Location("country")
            countryLocation.latitude = latitude
            countryLocation.longitude = longitude
            if (latitude + longitude == 0.0) {
                country.show = (if (MxCoreApplication.isAdmin) 1 else 0).toLong()
            } else if (isEurope(countryLocation)) {
                country.show = 0
            } else {
                country.show = 1
            }
            country.save(false)
        }
        context.contentResolver.notifyChange(MxInfoDBContract.Tracks.CONTENT_URI, null)
    }

    private fun isEurope(location: Location): Boolean {
        return location.longitude > -31 && location.longitude < 65
    }

    private fun updateNotification4Admin(meter: Int, payLoad: String) {
        if (!MxCoreApplication.isAdmin) {
            return
        }
        val text = "distance:$meter $payLoad"
        val longText = "distance:$meter $payLoad\nmuch more text #1"

        val intent = Intent(context, ActivityMapExtension::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val extras = Bundle()
        intent.putExtras(extras)
        val pendingIntent = PendingIntent.getActivity(context, 3210, intent, LocationJobService.pendingIntentFlags)
        val editPendingIntent = PendingIntent.getActivity(context, 3211, intent, LocationJobService.pendingIntentFlags)
        val showPendingIntent = PendingIntent.getActivity(context, 3212, intent, LocationJobService.pendingIntentFlags)

        Timber.w("send pendingintent updateNotification4Admin1 ${LocationJobService.pendingIntentFlags}")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(ADMIN_LOCATION_CHANNEL_ID, ADMIN_NOTIFICATION_CHANNEL_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(context, ADMIN_LOCATION_CHANNEL_ID)

        val notification = builder
            //.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                javaClass.simpleName.replace("Location", "").replace("Service", "")
                    .replace("Service", "") + " " + LocationJobService.SECONDS_UPDATE + "/" + LocationJobService.SECONDS_UPDATE_FAST
            )
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(longText))
            .setPriority(Notification.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .addAction(
                if (NotificationHelper.isDark()) R.drawable.ic_create_white_36dp else R.drawable.ic_create_black_36dp,
                "Edit",
                editPendingIntent
            )
            .addAction(if (NotificationHelper.isDark()) R.drawable.ic_flag_white_36dp else R.drawable.ic_flag_black_36dp, "Show", showPendingIntent)

        notificationManager.notify(NOTIFICATION_ID_ADMIN, notification.build())
    }

    private fun updateNotification(meter: Int, track_id: Long): String {
        val extra: String
        if (meter > DISTANCE_MIN_TO_NOTIFY) {
            extra = meter.toString() + "m > " + DISTANCE_MIN_TO_NOTIFY
            return extra
        }

        val text = context.getString(R.string.notification_small)
        val longText = context.getString(R.string.notification_big)
        val trackRecord = TracksRecord.get(track_id)
        val dateStr =
            DateHelper.getTimeStrFromMinutesDay(
                ((trackRecord!!.lastAsked + WAIT_TO_ASK - System.currentTimeMillis()) / 1000 / 60).toFloat().roundToInt()
            )
        if (trackRecord.lastAsked + WAIT_TO_ASK > System.currentTimeMillis()) {
            extra = "WAIT_TO_ASK $dateStr"
            return extra
        } else {
            extra = "Ok time:$dateStr"
        }

        trackRecord.lastAsked = System.currentTimeMillis()
        trackRecord.save(false)

        val intentShow = Intent(context, ActivityTrackDetail::class.java)
        intentShow.action = Intent.ACTION_MAIN
        intentShow.addCategory(Intent.CATEGORY_LAUNCHER)
        intentShow.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val extras = Bundle()
        extras.putString(FragmentUpDown.CONTENT_URI, Tracksges.CONTENT_URI.toString())
        extras.putLong(FragmentUpDown.RECORD_ID_LOCAL, track_id)
        intentShow.putExtras(extras)
        intentShow.putExtra(EDIT, false)
        val editIntent = Intent(intentShow)
        editIntent.putExtra(EDIT, true)
        val pendingIntent = PendingIntent.getActivity(context, 3210, intentShow, LocationJobService.pendingIntentFlags)
        val editPendingIntent = PendingIntent.getActivity(context, 3211, editIntent, LocationJobService.pendingIntentFlags)
        val showPendingIntent = PendingIntent.getActivity(context, 3212, intentShow, LocationJobService.pendingIntentFlags)

        Timber.w("send pendingintent updateNotification ${LocationJobService.pendingIntentFlags}")

        val builder = NotificationCompat.Builder(context, LOCATION_CHANNEL_ID)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel = NotificationChannel(ADMIN_LOCATION_CHANNEL_ID, ADMIN_NOTIFICATION_CHANNEL_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = builder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(trackRecord.trackname)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(longText))
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                if (NotificationHelper.isDark()) R.drawable.ic_create_white_36dp else R.drawable.ic_create_black_36dp,
                "Edit",
                editPendingIntent
            )
            .addAction(if (NotificationHelper.isDark()) R.drawable.ic_flag_white_36dp else R.drawable.ic_flag_black_36dp, "Show", showPendingIntent)

        notificationManager.notify(notificationID, notification.build())

        MxCoreApplication.trackEvent("vorort", trackRecord.trackname)
        return extra
    }

    companion object {

        private const val DISTANCE_MIN_TO_RECALC = 300
        private const val ACTION_FIRST = "first"
        internal const val ADMIN_LOCATION_CHANNEL_ID = "Admin Location Channel"
        internal const val ADMIN_NOTIFICATION_CHANNEL_NAME = "Admin Location Channel Name"
        internal const val LOCATION_CHANNEL_ID = "Location Channel"
        const val ACTION_IGNORED = "ignored"

        private val DISTANCE_MIN_TO_NOTIFY = (if (MxCoreApplication.isAdmin) 19500 else 1500).toFloat()
        private val WAIT_TO_ASK = (3600 * 1000 * if (MxCoreApplication.isAdmin) 1 else 24 * 4 * 30).toLong() //1 hour / 4 month

        private val NOTIFICATION_ID = 16241
        const val NOTIFICATION_ID_ADMIN = 16242
        const val DISTANCE_NEW = "distanceNew"
        const val EDIT = "edit"

        fun calculateDistanceOnTracks(mxMemDatabase: MxMemDatabase, location: Location?): List<TracksDistance> {
            val records = ArrayList<TracksDistance>()
            mxMemDatabase.beginTransaction()
            try {
                mxMemDatabase.tracksDistanceDao().deleteAll()
                val projection = arrayOf(Tracksges._ID, Tracksges.LATITUDE, Tracksges.LONGITUDE)
                val query = QueryHelper.buildTracksFilter(SQuery.newQuery(), AbstractMxInfoDBOpenHelper.Sources.TRACKSGES)
                val cursor = query.select(Tracksges.CONTENT_URI, projection)
                while (cursor.moveToNext()) {
                    var distance = -1
                    val tracksDistance = TracksDistance(
                        cursor.getLong(0),
                        SecHelper.entcryptXtude(cursor.getDouble(1)),
                        SecHelper.entcryptXtude(cursor.getDouble(2)),
                        distance.toLong()
                    )
                    if (location != null) {
                        val trackLocation = Location("track")
                        trackLocation.latitude = tracksDistance.lat
                        trackLocation.longitude = tracksDistance.lon
                        distance = location.distanceTo(trackLocation).roundToInt()
                        tracksDistance.distance = distance.toLong()
                    }
                    records.add(tracksDistance)
                }
                mxMemDatabase.tracksDistanceDao().insertAll(*records.toTypedArray())
                mxMemDatabase.setTransactionSuccessful()
            } finally {
                mxMemDatabase.endTransaction()
            }
            return records
        }
    }

}
