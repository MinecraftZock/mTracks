package info.mx.tracks.map

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.location.Location
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.DateHelper
import info.hannes.commonlib.LocationHelper.getFormatDistance
import info.mx.tracks.MxCoreApplication.Companion.isAdminOrDebug
import info.mx.tracks.R
import info.mx.tracks.base.FragmentRx
import info.mx.tracks.common.BitmapHelper
import info.mx.tracks.common.DistanceHelper.checkDistance4View
import info.mx.tracks.common.DistanceHelper.setDistanceString
import info.mx.tracks.common.setDayLayout
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.sqlite.*
import info.mx.tracks.sqlite.MxInfoDBContract.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*

class PoiDetailHeaderView(myContext: Context, attrs: AttributeSet?) : LinearLayout(myContext, attrs), KoinComponent {
    private val viewAddress: TextView
    private val lyAddress: View
    private val lyRating: View
    private val lyHours: View
    private val mainLayout: LinearLayout
    var detailStyle = PoiDetailStyle.DETAIL_TRACK
        private set
    private var prefs: MxPreferences? = null
    private val viewTrackAccess: ImageView
    private val viewCamera: ImageView
    private val viewCalendar: ImageView
    private val viewCountry: ImageView
    private val viewRating: RatingBar
    private val viewName: TextView
    private val viewMo: TextView
    private val viewTu: TextView
    private val viewWe: TextView
    private val viewTh: TextView
    private val viewFr: TextView
    private val viewSa: TextView
    private val viewSo: TextView
    private val viewDistance: TextView
    private var myLoc: Location? = null
    private val shortWeekdays: Array<String> = DateHelper.shortWeekdays
    private var fragmentRxCallBack: FragmentRx? = null
    private var currentPoi: TracksGesSumRecord? = null

    private val mxDatabase: MxDatabase by inject()

    interface PoiDetailHeaderListener {
        fun onHeaderLayoutUpdated(height: Int)
        fun onHeaderClicked(view: View, currentPoi: TracksGesSumRecord?)
    }

    @Suppress("unused")
    enum class PoiDetailStyle {
        DETAIL_TRACK, DETAIL_DEALER, DETAIL_STAGE, DETAIL_PLACE, ROUTE_DETAIL, ROUTE_LIST
    }

    @SuppressLint("SetTextI18n")
    private fun updateLayout(stageRecord: TrackstageRecord) {
        viewTrackAccess.setImageResource(R.drawable.marker_stage)
        viewCountry.visibility = VISIBLE
        lyAddress.visibility = GONE
        lyRating.visibility = VISIBLE
        lyHours.visibility = VISIBLE
        viewCamera.visibility = GONE
        viewCalendar.visibility = GONE
        if (stageRecord.country != null) {
            val countryValue = stageRecord.country.lowercase(Locale.getDefault()) + "2x"
            val countryResId =
                context.resources.getIdentifier(countryValue, "drawable", context.packageName)
            viewCountry.setImageResource(countryResId)
        }
        viewRating.rating = stageRecord.rating.toFloat()
        if (stageRecord.trackname == null) {
            val trackRecord =
                SQuery.newQuery().expr(Tracks.REST_ID, SQuery.Op.EQ, stageRecord.trackRestId)
                    .selectFirst<TracksRecord>(Tracks.CONTENT_URI)
            viewName.text =
                "<" + (if (trackRecord == null) "null " + stageRecord.trackRestId else trackRecord.trackname) + ">"
        } else {
            viewName.text = stageRecord.trackname
        }
        viewMo.setDayLayout(stageRecord.openmondays == 1L)
        viewTu.setDayLayout(stageRecord.opentuesdays == 1L)
        viewWe.setDayLayout(stageRecord.openwednesday == 1L)
        viewTh.setDayLayout(stageRecord.openthursday == 1L)
        viewFr.setDayLayout(stageRecord.openfriday == 1L)
        viewSa.setDayLayout(stageRecord.opensaturday == 1L)
        viewSo.setDayLayout(stageRecord.opensunday == 1L)
        viewMo.text = shortWeekdays[2]
        viewTu.text = shortWeekdays[3]
        viewWe.text = shortWeekdays[4]
        viewTh.text = shortWeekdays[5]
        viewFr.text = shortWeekdays[6]
        viewSa.text = shortWeekdays[7]
        viewSo.text = shortWeekdays[1]
        val distNew = stageRecord.insDistance.toInt()
        viewDistance.visibility = VISIBLE
        viewDistance.text = getFormatDistance(prefs!!.unitsKm, distNew)
        Timber.d("Route:%s", viewDistance.text.toString())
        viewDistance.setTextColor(ContextCompat.getColor(context, R.color.distance_font))
        onLayoutUpdateFinished()
    }

    private fun updateLayout(mxPlace: MxPlace) {
        viewTrackAccess.setImageResource(R.drawable.pin_poi_normal)
        lyAddress.visibility = VISIBLE
        lyRating.visibility = GONE
        lyHours.visibility = GONE
        viewCountry.visibility = GONE
        viewCamera.visibility = GONE
        viewCalendar.visibility = GONE
        viewRating.rating = mxPlace.rating
        viewName.text = mxPlace.name
        viewAddress.text = mxPlace.address
        viewDistance.visibility = GONE
        onLayoutUpdateFinished()
    }

    @SuppressLint("SetTextI18n")
    private fun updateLayout(poiRecord: TracksGesSumRecord) {
        currentPoi = poiRecord
        viewCountry.visibility = VISIBLE
        lyRating.visibility = VISIBLE
        lyHours.visibility = VISIBLE
        lyAddress.visibility = GONE
        var iconRes = R.drawable.flag_blau_hell
        when (poiRecord.trackaccess) {
            "R" -> iconRes = R.drawable.flag_race_hell
            "M" -> iconRes = R.drawable.flag_member_hell
            "D" -> iconRes = R.drawable.flag_dealer_large2x
        }
        viewTrackAccess.setImageBitmap(
            BitmapHelper.getBitmap(
                context,
                iconRes,
                poiRecord.trackstatus
            )
        )
        viewCamera.visibility = if (poiRecord.picturecount == "0") GONE else VISIBLE
        viewCalendar.visibility = if (poiRecord.eventcount == "0") GONE else VISIBLE
        if (poiRecord.country != null) {
            val countryValue = poiRecord.country.lowercase(Locale.getDefault()) + "2x"
            val countryResId =
                context.resources.getIdentifier(countryValue, "drawable", context.packageName)
            viewCountry.setImageResource(countryResId)
            fragmentRxCallBack!!.addDisposable(
                mxDatabase.commentDao().avgByTrackId(poiRecord.restId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ rating: Float? ->
                        viewRating.rating = rating!!
                    }) { Timber.e("Subscribing to registerRx failed") }
            )
        }
        viewName.text =
            (if (isAdminOrDebug && poiRecord.approved == -1L) "--" else "") + poiRecord.trackname
        viewMo.setDayLayout(poiRecord.openmondays == 1L)
        viewTu.setDayLayout(poiRecord.opentuesdays == 1L)
        viewWe.setDayLayout(poiRecord.openwednesday == 1L)
        viewTh.setDayLayout(poiRecord.openthursday == 1L)
        viewFr.setDayLayout(poiRecord.openfriday == 1L)
        viewSa.setDayLayout(poiRecord.opensaturday == 1L)
        viewSo.setDayLayout(poiRecord.opensunday == 1L)
        viewMo.text = shortWeekdays[2]
        viewTu.text = shortWeekdays[3]
        viewWe.text = shortWeekdays[4]
        viewTh.text = shortWeekdays[5]
        viewFr.text = shortWeekdays[6]
        viewSa.text = shortWeekdays[7]
        viewSo.text = shortWeekdays[1]
        checkDistance4View(poiRecord, viewDistance, myLoc)
        onLayoutUpdateFinished()
    }

    fun setLocation(location: Location?) {
        myLoc = location
    }

    private fun setParentLayoutParams(layoutSty: PoiDetailStyle) {
        this.isClickable = true
        when (layoutSty) {
            PoiDetailStyle.DETAIL_TRACK -> {}
            else -> Unit
        }
        this.isClickable = true
        this.orientation = HORIZONTAL
    }

    fun showStageDetail(stageId: Long, loaderManager: LoaderManager) {
        detailStyle = PoiDetailStyle.DETAIL_STAGE
        setParentLayoutParams(detailStyle)
        val bundle = Bundle()
        bundle.putLong(TRACK_ID, stageId)
        Timber.d("Stage restart %s", stageId)
        loaderManager.restartLoader(LOADER_STAGE, bundle, LoadingPoi(loaderManager))
    }

    fun showPlaceDetail(mxPlace: MxPlace) {
        detailStyle = PoiDetailStyle.DETAIL_PLACE
        setParentLayoutParams(detailStyle)
        updateLayout(mxPlace)
    }

    fun showTrackDetail(trackId: Long, loaderManager: LoaderManager) {
        detailStyle = PoiDetailStyle.DETAIL_TRACK
        setParentLayoutParams(detailStyle)
        val bundle = Bundle()
        bundle.putLong(TRACK_ID, trackId)
        Timber.d("PoiLoad restart %s", trackId)
        loaderManager.restartLoader(LOADER_TRACK, bundle, LoadingPoi(loaderManager))
    }

    private inner class LoadingPoi(private val poiLoaderManager: LoaderManager) :
        LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
            return when (loader) {
                LOADER_TRACK -> SQuery.newQuery().expr(
                    TracksGesSum._ID,
                    SQuery.Op.EQ,
                    bundle!!.getLong(TRACK_ID)
                )
                    .createSupportLoader(TracksGesSum.CONTENT_URI, null)
                LOADER_STAGE -> SQuery.newQuery().expr(
                    Trackstage._ID,
                    SQuery.Op.EQ,
                    bundle!!.getLong(TRACK_ID)
                )
                    .createSupportLoader(Trackstage.CONTENT_URI, null)
                LOADER_ROUTE -> SQuery.newQuery()
                    .expr(
                        Route.TRACK_CLIENT_ID,
                        SQuery.Op.EQ,
                        bundle!!.getLong(TRACK_ID)
                    )
                    .createSupportLoader(
                        Route.CONTENT_URI,
                        null,
                        Route.CREATED
                    )
                else -> SQuery.newQuery()
                    .expr(
                        Route.TRACK_CLIENT_ID,
                        SQuery.Op.EQ,
                        bundle!!.getLong(TRACK_ID)
                    )
                    .createSupportLoader(
                        Route.CONTENT_URI,
                        null,
                        Route.CREATED
                    )
            }
        }

        override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                when (loader.id) {
                    LOADER_TRACK -> {
                        val poiRecord = TracksGesSumRecord.fromCursor(cursor)
                        updateLayout(poiRecord)
                        val bundle = Bundle()
                        bundle.putLong(TRACK_ID, poiRecord.id)
                        poiLoaderManager.restartLoader(
                            LOADER_ROUTE,
                            bundle,
                            LoadingPoi(poiLoaderManager)
                        )
                    }
                    LOADER_STAGE -> {
                        val stageRecord = TrackstageRecord.fromCursor(cursor)
                        updateLayout(stageRecord)
                    }
                    LOADER_ROUTE -> {
                        Timber.i("onLoadFinished Route:%s", cursor.count)
                        if (cursor.isBeforeFirst) {
                            cursor.moveToFirst()
                        }
                        if (cursor.count > 0) {
                            val routeRecord = RouteRecord.fromCursor(cursor)
                            setDistanceString(routeRecord, viewDistance)
                        }
                    }
                }
            }
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

    fun setCallBackFragment(fragmentRx: FragmentRx?) {
        fragmentRxCallBack = fragmentRx
    }

    private fun onLayoutUpdateFinished() {
        mainLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val height = mainLayout.measuredHeight
        if (fragmentRxCallBack is PoiDetailHeaderListener) {
            (fragmentRxCallBack as PoiDetailHeaderListener?)!!.onHeaderLayoutUpdated(height)
        }
    }

    companion object {
        private const val LOADER_TRACK = 10
        private const val LOADER_STAGE = 11
        private const val LOADER_ROUTE = 12
        private const val TRACK_ID = "track_id"
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.item_track, this, true)
        mainLayout = findViewById(R.id.layoutPoiHeaderMain)
        mainLayout.setOnClickListener { view: View ->
            if (fragmentRxCallBack is PoiDetailHeaderListener) {
                (fragmentRxCallBack as PoiDetailHeaderListener?)!!.onHeaderClicked(view, currentPoi)
            }
        }
        setParentLayoutParams(PoiDetailStyle.DETAIL_TRACK)
        if (!isInEditMode) {
            prefs = MxPreferences.getInstance()
        }
        viewTrackAccess = findViewById(R.id.tr_track_access)
        viewCamera = findViewById(R.id.tr_camera)
        viewCalendar = findViewById(R.id.tr_calendar)
        viewCountry = findViewById(R.id.tr_country)
        viewRating = findViewById(R.id.tr_ratingBar)
        viewName = findViewById(R.id.tr_name)
        viewMo = findViewById(R.id.tr_mo)
        viewTu = findViewById(R.id.tr_tu)
        viewWe = findViewById(R.id.tr_we)
        viewTh = findViewById(R.id.tr_th)
        viewFr = findViewById(R.id.tr_fr)
        viewSa = findViewById(R.id.tr_sa)
        viewSo = findViewById(R.id.tr_so)
        viewAddress = findViewById(R.id.tr_address)
        viewDistance = findViewById(R.id.tr_detail_distance)
        lyAddress = findViewById(R.id.tr_layoutAddress)
        lyRating = findViewById(R.id.tr_layoutRating)
        lyHours = findViewById(R.id.tr_layoutHours)
    }
}