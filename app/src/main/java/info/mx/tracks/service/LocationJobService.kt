package info.mx.tracks.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import androidx.core.app.NotificationCompat
import androidx.work.Configuration
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.common.NotificationHelper
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.CapturedLatLng
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.tools.PermissionHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

/**
 * To test run
 * adb root
 * adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
 */
class LocationJobService : JobService(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, KoinComponent {
    private var jobParameters: JobParameters? = null

    val permissionHelper: PermissionHelper by inject()

    val mxDatabase: MxDatabase by inject()

    private var googleApiClient: GoogleApiClient? = null
    private var mLocationCallback: LocationCallback? = null

    private val isConnected = googleApiClient != null && googleApiClient!!.isConnected

    private val requestDay = REQUEST_DAY

    init {
        // need minimum 1000 job ids
        Configuration.Builder().setJobSchedulerJobIdRange(0, 1000).build()
    }

    override fun onStartJob(jobParam: JobParameters): Boolean {
        jobParameters = jobParam

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Thread {
                    Timber.i("onLocationResult")

                    // TODO this is just a debug record
                    val extra = "onLocationResult " + locationResult
                    dbLog(extra)

                    for (location in locationResult.locations) {
                        onNewLocation(location)
                    }
                    Timber.i("jobFinished onCreate")
                    jobFinished(jobParameters, false)
                }.start()
            }
        }
        setUpLocationClientIfNeeded()

        return permissionHelper.hasLocationPermission() // true: we are not finished and wait till location update
    }

    override fun onStopJob(jobParam: JobParameters): Boolean {
        Timber.i("onStopJob")
        return false
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        stopLocationUpdates()
        updateNotification4Admin(0, "destroy")
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        if (permissionHelper.hasLocationPermission()) {
            val handlerThread = HandlerThread("location handlerthread")
            handlerThread.start()
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(requestDay, mLocationCallback!!, handlerThread.looper)
        }
    }

    private fun stopLocationUpdates() {
        if (isConnected) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback!!)
        }
    }

    @SuppressLint("CheckResult")
    private fun setUpLocationClientIfNeeded(): Boolean {
        var extra = "Job started " + permissionHelper.hasLocationPermission()
        if (permissionHelper.hasLocationPermission()) {
            Timber.d("It has location permission")
            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build()
            }
            googleApiClient!!.connect()
            extra += " " + googleApiClient!!.isConnecting + "/" + googleApiClient!!.isConnected
        }

        // TODO this is just a debug record
        dbLog(extra)

        return permissionHelper.hasLocationPermission()
    }

    private fun dbLog(extra: String) {
        val debugLatLon = CapturedLatLng()
        debugLatLon.lat = 0.0
        debugLatLon.lon = 0.0
        debugLatLon.distanceToLast = 0
        debugLatLon.action = RecalculateDistance.ACTION_IGNORED
        debugLatLon.extra = extra
        debugLatLon.time = System.currentTimeMillis()
        debugLatLon.trackname = "LocationJob"
        mxDatabase.capturedLatLngDao().insertAll(debugLatLon)
    }

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (isConnected) {
            stopLocationUpdates()
        }
        updateNotification4Admin(0, "onConnectionFailed")
        Timber.i("jobFinished onConnectionFailed")
        jobFinished(jobParameters, false)
    }

    @SuppressLint("CheckResult")
    private fun onNewLocation(location: Location) {
        val recalcDistance = RecalculateDistance(this)
        recalcDistance.recalculateTracks(location, "job")

        Timber.i("jobFinished onNewLocation")
        jobFinished(jobParameters, false)
    }

    private fun updateNotification4Admin(meter: Int, payLoad: String) {
        if (!MxCoreApplication.isAdmin) {
            return
        }
        val text = "distance:$meter $payLoad"
        val longText = "distance:$meter $payLoad\nmuch more text #1"

        val intent = Intent(this, ActivityMapExtension::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val extras = Bundle()
        intent.putExtras(extras)
        val pendingIntent = PendingIntent.getActivity(this, 3210, intent, pendingIntentFlags)
        val editPendingIntent = PendingIntent.getActivity(this, 3211, intent, pendingIntentFlags)
        val showPendingIntent = PendingIntent.getActivity(this, 3212, intent, pendingIntentFlags)

        Timber.w("send pendingintent updateNotification4Admin2 $pendingIntentFlags")

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val notificationChannel =
                NotificationChannel(RecalculateDistance.ADMIN_LOCATION_CHANNEL_ID, RecalculateDistance.ADMIN_NOTIFICATION_CHANNEL_NAME, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(this, RecalculateDistance.LOCATION_CHANNEL_ID)

        val notification = builder
            //.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(
                javaClass.simpleName.replace("Location", "").replace("Service", "")
                    .replace("Service", "") + " " + SECONDS_UPDATE + "/" + SECONDS_UPDATE_FAST
            )
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(longText))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .addAction(
                if (NotificationHelper.isDark()) R.drawable.ic_create_white_36dp else R.drawable.ic_create_black_36dp,
                "Edit",
                editPendingIntent
            )
            .addAction(if (NotificationHelper.isDark()) R.drawable.ic_flag_white_36dp else R.drawable.ic_flag_black_36dp, "Show", showPendingIntent)

        notificationManager.notify(RecalculateDistance.NOTIFICATION_ID_ADMIN, notification.build())
    }

    companion object {

        private const val JOB_ID_DISTANCE = 0
        const val SECONDS_UPDATE = 30
        const val SECONDS_UPDATE_FAST = 10

        // force a new location every X minutes
        val REQUEST_DAY = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, SECONDS_UPDATE * 1000L).apply {
            setMinUpdateDistanceMeters(200f) // meters
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        // schedule the start of the service every 5 - 60 seconds
        fun scheduleJob(context: Context) {
            val applicationScope = MainScope()
            applicationScope.launch {
                if (MxPreferences.getInstance().agreeTrackSurveillance) {
                    val serviceComponent = ComponentName(context, LocationJobService::class.java)
                    val builder = JobInfo.Builder(JOB_ID_DISTANCE, serviceComponent)
                    builder.setPersisted(true)

                    // not for periodic
                    builder.setMinimumLatency((5 * 1000).toLong()) // wait at least
                    builder.setOverrideDeadline((120 * 1000).toLong()) // maximum delay

                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //     builder.setPeriodic(SECONDS_UPDATE_FAST * 60 * 1000, 20 * 1000);
                    // } else {
                    //     builder.setPeriodic(SECONDS_UPDATE_FAST * 60 * 1000);
                    // }

                    // builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
                    // builder.setRequiresDeviceIdle(true); // device should be idle
                    // builder.setRequiresCharging(false); // we don't care if the device is charging or not
                    val jobScheduler: JobScheduler? = context.getSystemService(JobScheduler::class.java)
                    jobScheduler?.schedule(builder.build())
                }
            }
        }

        fun restartService(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancelAll()
            scheduleJob(context)
        }

        const val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    }

}
