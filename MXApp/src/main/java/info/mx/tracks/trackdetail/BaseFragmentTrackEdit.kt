package info.mx.tracks.trackdetail

import info.hannes.commonlib.DateHelper.shortWeekdays
import info.mx.tracks.MxApplication.Companion.isGoogleTests
import info.mx.tracks.service.LocationJobService.Companion.REQUEST_DAY
import info.mx.tracks.MxCoreApplication.Companion.doSync
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.sqlite.TracksRecord
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import info.mx.tracks.R
import info.mx.tracks.common.On3StateClickListener
import android.content.Intent
import info.mx.tracks.service.RecalculateDistance
import android.os.Looper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.StatusHelper
import info.mx.tracks.sqlite.TrackstageRecord
import info.mx.tracks.map.FragmentMapScroll
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.prefs.MxPreferences
import android.os.Vibrator
import android.os.Build
import android.os.VibrationEffect
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.ops.OpGetLatLngOperation
import android.content.DialogInterface
import android.graphics.Point
import android.location.Location
import android.provider.Settings
import android.view.*
import android.widget.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.robotoworks.mechanoid.ops.OperationExecutor
import com.robotoworks.mechanoid.ops.OperationExecutorCallbacks
import com.robotoworks.mechanoid.ops.OperationResult
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.BuildConfig
import info.mx.tracks.databinding.FragmentTrackEditBinding
import info.mx.tracks.ops.AbstractOpGetLatLngOperation
import info.mx.tracks.ops.AbstractOpPostTrackAppovedOperation
import timber.log.Timber
import java.lang.NullPointerException
import java.util.*
import kotlin.math.roundToInt

abstract class BaseFragmentTrackEdit : FragmentBase(), GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected var recordTrack: TracksRecord? = null
    private var id: Long = 0
    private var adapterAccess: TrackAccessSpinnerAdapter? = null

    protected var map: GoogleMap? = null
    private var marker: Marker? = null
    private var getMoviesOperationExecutor: OperationExecutor? = null

    protected var stageId: Long? = null
    private var googleApiClient: GoogleApiClient? = null
    private lateinit var locationCallback: LocationCallback

    private var _binding: FragmentTrackEditBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTrackEditBinding.inflate(inflater, container, false)
        val view = binding.root

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val recalcDistance = RecalculateDistance(requireContext())
                    recalcDistance.recalculateTracks(location!!, "edit")
                }
            }
        }

        val shortWeekdays = shortWeekdays
        binding.teDetailMo.text = shortWeekdays[2]
        binding.teDetailDi.text = shortWeekdays[3]
        binding.teDetailMi.text = shortWeekdays[4]
        binding.teDetailDo.text = shortWeekdays[5]
        binding.teDetailFr.text = shortWeekdays[6]
        binding.teDetailSa.text = shortWeekdays[7]
        binding.teDetailSo.text = shortWeekdays[1]
        binding.teApproved.setOnClickListener(object : On3StateClickListener() {
            override fun onClick(view: View) {
                super.onClick(view)
                val myId = view.tag as Long
                if (isGoogleTests) {
                    Toast.makeText(
                        requireContext(),
                        "you test the app, event is ignored",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = AbstractOpPostTrackAppovedOperation.newIntent(myId, level - 1)
                    Ops.execute(intent)
                }
            }
        })
        binding.imageMo.setOnClickListener(On3StateClickListener())
        binding.imageDi.setOnClickListener(On3StateClickListener())
        binding.imageMi.setOnClickListener(On3StateClickListener())
        binding.imageDo.setOnClickListener(On3StateClickListener())
        binding.imageFr.setOnClickListener(On3StateClickListener())
        binding.imageSa.setOnClickListener(On3StateClickListener())
        binding.imageSo.setOnClickListener(On3StateClickListener())
        binding.teDetailMx.setOnClickListener(On3StateClickListener())
        binding.teDetailQuad.setOnClickListener(On3StateClickListener())
        binding.teDetail4x4.setOnClickListener(On3StateClickListener())
        binding.teDetailEnduro.setOnClickListener(On3StateClickListener())
        binding.teDetailUtv.setOnClickListener(On3StateClickListener())
        binding.teDetailCamping.setOnClickListener(On3StateClickListener())
        binding.teDetailShower.setOnClickListener(On3StateClickListener())
        binding.teDetailElectricity.setOnClickListener(On3StateClickListener())
        binding.teDetailBikecleaning.setOnClickListener(On3StateClickListener())
        binding.teDetailKids.setOnClickListener(On3StateClickListener())
        binding.teDetailSx.setOnClickListener(On3StateClickListener())
        binding.imageToggleLeft.setOnClickListener { toggleMap() }
        binding.imageToggleBottom.setOnClickListener { toggleMap() }
        binding.teDifficult.isFocusableInTouchMode = true
        binding.teDifficult.isClickable = true

        //init map size
        val params = binding.teLayoutMap.layoutParams
        params.height = smallRatio
        binding.teLayoutMap.layoutParams = params

        adapterAccess = TrackAccessSpinnerAdapter(requireContext())
        binding.teDetailAccess.adapter = adapterAccess
        setHasOptionsMenu(true)
        setUpMapIfNeeded()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleMap() {
        // display size
        val display = requireActivity().windowManager.defaultDisplay
        val size = getDisplaySize(display)
        val params = binding.teLayoutMap.layoutParams
        if (binding.teLayoutMap.height == smallRatio) {
            // bigger
            params.height = size.y / 100 * 70
            binding.imageToggleBottom.visibility = View.VISIBLE
        } else {
            // small
            binding.imageToggleBottom.visibility = View.GONE
            params.height = smallRatio
            if (marker != null) {
                map!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        marker!!.position, 15f
                    )
                )
            }
        }
        binding.teLayoutMap.layoutParams = params
    }

    // scaling
    private val smallRatio: Int
        get() {
            // scaling
            val dp = Math.round(requireActivity().resources.getDimension(R.dimen.thumbnail_size_dp))
            val dpExact = (dp / requireActivity().resources.displayMetrics.density).toInt()
            val ratio = dp.toFloat() / dpExact
            return Math.round(150 * ratio)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getMoviesOperationExecutor!!.saveState(outState)
    }

    override fun onPause() {
        mask2Record(false)
        stopLocationUpdates()
        super.onPause()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (permissionHelper.hasLocationPermission()) {
            LocationServices.getFusedLocationProviderClient(requireContext())
                .requestLocationUpdates(REQUEST_DAY, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            LocationServices.getFusedLocationProviderClient(requireContext())
                .removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("DefaultLocale")
    open fun fillMask(newId: Long) {
        binding.teDetailNameEd.requestFocus()
        if (newId > 0) {
            if (recordTrack == null || recordTrack!!.id != newId) {
                recordTrack = TracksRecord.get(newId)
            }
            if (recordTrack != null) {
                addMarker(
                    LatLng(
                        SecHelper.entcryptXtude(
                            recordTrack!!.latitude
                        ), SecHelper.entcryptXtude(recordTrack!!.longitude)
                    )
                )
            }
        } else {
            if (recordTrack == null) {
                recordTrack = TracksRecord()
                recordTrack!!.trackaccess = "N"
            }
        }
        if (recordTrack != null) {
            binding.teDetailLocation.setSelection(recordTrack!!.indoor.toInt())
            binding.teDetailNameEd.setText(recordTrack!!.trackname)
            binding.teDetailSoil.setSelection(recordTrack!!.soiltype.toInt())
            binding.teDetailAccess.setSelection(adapterAccess!!.getPositionById(recordTrack!!.trackaccess))
        }
        for (i in 0 until binding.teStatus.adapter.count) {
            if (binding.teStatus.adapter.getItem(i) ==
                StatusHelper.getLongStatusText(requireActivity(), recordTrack!!.trackstatus)
            ) {
                binding.teStatus.setSelection(i)
                break
            }
        }
        binding.teApproved.visibility = View.GONE
        binding.teApproved.setImageLevel((recordTrack!!.approved + 1).toInt())
        binding.teApproved.tag = recordTrack!!.restId
        binding.imageMo.setImageLevel((recordTrack!!.openmondays + 1).toInt())
        binding.imageDi.setImageLevel((recordTrack!!.opentuesdays + 1).toInt())
        binding.imageMi.setImageLevel((recordTrack!!.openwednesday + 1).toInt())
        binding.imageDo.setImageLevel((recordTrack!!.openthursday + 1).toInt())
        binding.imageFr.setImageLevel((recordTrack!!.openfriday + 1).toInt())
        binding.imageSa.setImageLevel((recordTrack!!.opensaturday + 1).toInt())
        binding.imageSo.setImageLevel((recordTrack!!.opensunday + 1).toInt())
        binding.teDetailMoHour.setText(recordTrack!!.hoursmonday)
        binding.teDetailDiHour.setText(recordTrack!!.hourstuesday)
        binding.teDetailMiHour.setText(recordTrack!!.hourswednesday)
        binding.teDetailDoHour.setText(recordTrack!!.hoursthursday)
        binding.teDetailFrHour.setText(recordTrack!!.hoursfriday)
        binding.teDetailSaHour.setText(recordTrack!!.hourssaturday)
        binding.teDetailSoHour.setText(recordTrack!!.hourssunday)
        binding.teDetailWebsite.setText(SecHelper.decryptB64(recordTrack!!.url))
        binding.teDetailPhone.setText(SecHelper.decryptB64(recordTrack!!.phone))
        binding.teDetailContact.setText(SecHelper.decryptB64(recordTrack!!.contact))
        binding.teDetailMx.setImageLevel((recordTrack!!.mxtrack + 1).toInt())
        binding.teDetailQuad.setImageLevel((recordTrack!!.quad + 1).toInt())
        binding.teDetail4x4.setImageLevel((recordTrack!!.a4x4 + 1).toInt())
        binding.teDetailEnduro.setImageLevel((recordTrack!!.enduro + 1).toInt())
        binding.teDetailUtv.setImageLevel((recordTrack!!.utv + 1).toInt())
        binding.teDetailCamping.setImageLevel((recordTrack!!.camping + 1).toInt())
        binding.teDetailShower.setImageLevel((recordTrack!!.shower + 1).toInt())
        binding.teDetailElectricity.setImageLevel((recordTrack!!.electricity + 1).toInt())
        binding.teDetailBikecleaning.setImageLevel((recordTrack!!.cleaning + 1).toInt())
        binding.teDetailKids.setImageLevel((recordTrack!!.kidstrack + 1).toInt())
        binding.teDetailSx.setImageLevel((recordTrack!!.supercross + 1).toInt())
        binding.teLayoutCamping.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutShower.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutElect.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutBikclean.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutKids.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutFees.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutFacebook.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutLength.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutSoil.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutAdress.visibility = if (recordTrack!!.trackaccess != "D") View.GONE else View.VISIBLE
        binding.teLayoutSx.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teLayoutLicense.visibility = if (recordTrack!!.trackaccess == "D") View.GONE else View.VISIBLE
        binding.teDetailNotes.setText(recordTrack!!.notes)
        binding.teDetailFees.setText(recordTrack!!.fees)
        binding.teDetailFacebook.setText(SecHelper.decryptB64(recordTrack!!.facebook))
        binding.teNoiseLimit.setText(recordTrack!!.noiselimit)
        binding.teDetailLength.setText(String.format("%d", recordTrack!!.tracklength))
        binding.teDetailAdress.setText(SecHelper.decryptB64(recordTrack!!.adress))
        binding.teDetailLicense.setText(recordTrack!!.licence)
        binding.teDifficult.rating = recordTrack!!.schwierigkeit.toFloat()
    }

    fun hasDefaultLatLon(): Boolean {
        var lat = DEFAULT_LAT
        var lon = DEFAULT_LON
        marker?.let {
            lat = Math.round(it.position.latitude * fak5).toDouble() / fak5
            lon = Math.round(it.position.longitude * fak5).toDouble() / fak5
        }
        Timber.d("lat/lon:$lat/$lon")
        return lat == DEFAULT_LAT && lon == DEFAULT_LON
    }

    /**
     * The changes in recordTrack are just for the customer, and they have no affect
     *
     * @param save make a real save action
     * @return if successful
     */
    @SuppressLint("MissingPermission")
    fun mask2Record(save: Boolean): Boolean {
        if (recordTrack == null || activity == null) {
            return true
        }
        var changed = false
        if (binding.teDetailNameEd.text.toString() == "") {
            binding.teDetailNameEd.error = getString(R.string.trackname_required)
            return false
        } else if (hasDefaultLatLon()) {
            binding.teDetailNameEd.error = getString(R.string.default_latlon)
            return !save
        } else {
            binding.teDetailNameEd.error = null
        }
        val stage = TrackstageRecord()
        stage.trackRestId = recordTrack!!.restId
        val neu = recordTrack!!.restId == 0L
        if (neu && binding.teDetailNameEd.text.toString().trim { it <= ' ' } != "") {
            stage.trackname = binding.teDetailNameEd.text.toString().trim { it <= ' ' }
            recordTrack!!.trackname = binding.teDetailNameEd.text.toString().trim { it <= ' ' }
            changed = true
        }
        if (!neu && binding.teDetailNameEd.text.toString().trim { it <= ' ' } != "") {
            if (getValue(recordTrack!!.trackname) != binding.teDetailNameEd.text.toString().trim { it <= ' ' }
            ) {
                stage.trackname = binding.teDetailNameEd.text.toString().trim { it <= ' ' }
                recordTrack!!.trackname = binding.teDetailNameEd.text.toString().trim { it <= ' ' }
                changed = true
            }
        }
        if (neu || recordTrack!!.indoor != binding.teDetailLocation.selectedItemPosition.toLong()) {
            stage.indoor = binding.teDetailLocation.selectedItemPosition.toLong()
            changed = true
            recordTrack!!.indoor = stage.indoor
        }
        if (neu || recordTrack!!.soiltype != binding.teDetailSoil.selectedItemPosition.toLong()) {
            stage.soiltype = binding.teDetailSoil.selectedItemPosition.toLong()
            changed = true
            recordTrack!!.soiltype = stage.soiltype
        }
        var status = recordTrack!!.trackstatus
        if (status == null) status = ""
        if (neu || !status.contentEquals(
                StatusHelper.getShortStatusText(
                    requireActivity(),
                    binding.teStatus.selectedItem as String
                )
            )
        ) {
            stage.trackstatus = StatusHelper.getShortStatusText(
                requireActivity(),
                binding.teStatus.selectedItem as String
            ) as String
            changed = true
            recordTrack!!.trackstatus = stage.trackstatus
        }
        if (neu || getValue(recordTrack!!.trackaccess) != (binding.teDetailAccess.selectedItem as TrackAccessElement).id) {
            stage.trackaccess = (binding.teDetailAccess.selectedItem as TrackAccessElement).id
            changed = true
            recordTrack!!.trackaccess = stage.trackaccess
        }
        if (recordTrack!!.openmondays != (binding.imageMo.drawable.level - 1).toLong()) {
            stage.openmondays = (binding.imageMo.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.openmondays = stage.openmondays
        }
        if (recordTrack!!.opentuesdays != (binding.imageDi.drawable.level - 1).toLong()) {
            stage.opentuesdays = (binding.imageDi.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.opentuesdays = stage.opentuesdays
        }
        if (recordTrack!!.openwednesday != (binding.imageMi.drawable.level - 1).toLong()) {
            stage.openwednesday = (binding.imageMi.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.openwednesday = stage.openwednesday
        }
        if (recordTrack!!.openthursday != (binding.imageDo.drawable.level - 1).toLong()) {
            stage.openthursday = (binding.imageDo.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.openthursday = stage.openthursday
        }
        if (recordTrack!!.openfriday != (binding.imageFr.drawable.level - 1).toLong()) {
            stage.openfriday = (binding.imageFr.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.openfriday = stage.openfriday
        }
        if (recordTrack!!.opensaturday != (binding.imageSa.drawable.level - 1).toLong()) {
            stage.opensaturday = (binding.imageSa.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.opensaturday = stage.opensaturday
        }
        if (recordTrack!!.opensunday != (binding.imageSo.drawable.level - 1).toLong()) {
            stage.opensunday = (binding.imageSo.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.opensunday = stage.opensunday
        }
        if (recordTrack!!.mxtrack != (binding.teDetailMx.drawable.level - 1).toLong()) {
            stage.mxTrack = (binding.teDetailMx.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.mxtrack = stage.mxTrack
        }
        if (recordTrack!!.quad != (binding.teDetailQuad.drawable.level - 1).toLong()) {
            stage.quad = (binding.teDetailQuad.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.quad = stage.quad
        }
        if (recordTrack!!.a4x4 != (binding.teDetail4x4.drawable.level - 1).toLong()) {
            stage.a4X4 = (binding.teDetail4x4.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.a4x4 = stage.a4X4
        }
        if (recordTrack!!.enduro != (binding.teDetailEnduro.drawable.level - 1).toLong()) {
            stage.enduro = (binding.teDetailEnduro.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.enduro = stage.enduro
        }
        if (recordTrack!!.utv != (binding.teDetailUtv.drawable.level - 1).toLong()) {
            stage.utv = (binding.teDetailUtv.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.utv = stage.utv
        }
        if (recordTrack!!.camping != (binding.teDetailCamping.drawable.level - 1).toLong()) {
            stage.camping = (binding.teDetailCamping.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.camping = stage.camping
        }
        if (recordTrack!!.shower != (binding.teDetailShower.drawable.level - 1).toLong()) {
            stage.shower = (binding.teDetailShower.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.shower = stage.shower
        }
        if (recordTrack!!.electricity != (binding.teDetailElectricity.drawable.level - 1).toLong()) {
            stage.electricity = (binding.teDetailElectricity.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.electricity = stage.electricity
        }
        if (recordTrack!!.cleaning != (binding.teDetailBikecleaning.drawable.level - 1).toLong()) {
            stage.cleaning = (binding.teDetailBikecleaning.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.cleaning = stage.cleaning
        }
        if (recordTrack!!.kidstrack != (binding.teDetailKids.drawable.level - 1).toLong()) {
            stage.kidstrack = (binding.teDetailKids.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.kidstrack = stage.kidstrack
        }
        if (recordTrack!!.supercross != (binding.teDetailSx.drawable.level - 1).toLong()) {
            stage.supercross = (binding.teDetailSx.drawable.level - 1).toLong()
            changed = true
            recordTrack!!.supercross = stage.supercross
        }
        if (getValue(recordTrack!!.hoursmonday) != binding.teDetailMoHour.text.toString()) {
            stage.hoursmonday = binding.teDetailMoHour.text.toString()
            changed = true
            recordTrack!!.hoursmonday = stage.hoursmonday
        }
        if (getValue(recordTrack!!.hourstuesday) != binding.teDetailDiHour.text.toString()) {
            stage.hourstuesday = binding.teDetailDiHour.text.toString()
            changed = true
            recordTrack!!.hourstuesday = stage.hourstuesday
        }
        if (getValue(recordTrack!!.hourswednesday) != binding.teDetailMiHour.text.toString()) {
            stage.hourswednesday = binding.teDetailMiHour.text.toString()
            changed = true
            recordTrack!!.hourswednesday = stage.hourswednesday
        }
        if (getValue(recordTrack!!.hoursthursday) != binding.teDetailDoHour.text.toString()) {
            stage.hoursthursday = binding.teDetailDoHour.text.toString()
            changed = true
            recordTrack!!.hoursthursday = stage.hoursthursday
        }
        if (getValue(recordTrack!!.hoursfriday) != binding.teDetailFrHour.text.toString()) {
            stage.hoursfriday = binding.teDetailFrHour.text.toString()
            changed = true
            recordTrack!!.hoursfriday = stage.hoursfriday
        }
        if (getValue(recordTrack!!.hourssaturday) != binding.teDetailSaHour.text.toString()) {
            stage.hourssaturday = binding.teDetailSaHour.text.toString()
            changed = true
            recordTrack!!.hourssaturday = stage.hourssaturday
        }
        if (getValue(recordTrack!!.hourssunday) != binding.teDetailSoHour.text.toString()) {
            stage.hourssunday = binding.teDetailSoHour.text.toString()
            changed = true
            recordTrack!!.hourssunday = stage.hourssunday
        }
        if (getValue(SecHelper.decryptB64(recordTrack!!.url)) != binding.teDetailWebsite.text.toString()) {
            stage.url = binding.teDetailWebsite.text.toString()
            changed = true
            recordTrack!!.url = stage.url
        }
        if (getValue(SecHelper.decryptB64(recordTrack!!.phone)) != binding.teDetailPhone.text.toString()) {
            stage.phone = binding.teDetailPhone.text.toString()
            changed = true
            recordTrack!!.phone = stage.phone
        }
        if (getValue(SecHelper.decryptB64(recordTrack!!.contact)) != binding.teDetailContact.text.toString()) {
            stage.contact = binding.teDetailContact.text.toString()
            changed = true
            recordTrack!!.contact = stage.contact
        }
        if (getValue(recordTrack!!.notes) != binding.teDetailNotes.text.toString()) {
            stage.notes = binding.teDetailNotes.text.toString()
            changed = true
            recordTrack!!.notes = stage.notes
        }
        if (getValue(recordTrack!!.fees) != binding.teDetailFees.text.toString()) {
            stage.fees = binding.teDetailFees.text.toString()
            changed = true
            recordTrack!!.fees = stage.fees
        }
        if (getValue(SecHelper.decryptB64(recordTrack!!.facebook)) != binding.teDetailFacebook.text.toString()) {
            stage.facebook = binding.teDetailFacebook.text.toString()
            changed = true
            recordTrack!!.facebook = SecHelper.encryptB64(stage.facebook)
        }
        if (getValue(recordTrack!!.noiselimit) != binding.teNoiseLimit.text.toString()) {
            stage.noiselimit = binding.teNoiseLimit.text.toString()
            changed = true
            recordTrack!!.noiselimit = stage.noiselimit
        }
        if (binding.teDetailLength.text.toString() != "" && recordTrack!!.tracklength != binding.teDetailLength.text.toString().toLong()
        ) {
            if (binding.teDetailLength.text.toString() != "") {
                stage.tracklength = binding.teDetailLength.text.toString().toLong()
                recordTrack!!.tracklength = stage.tracklength
                changed = true
            }
        }
        if (getValue(recordTrack!!.licence) != binding.teDetailLicense.text.toString()) {
            stage.licence = binding.teDetailLicense.text.toString()
            changed = true
            recordTrack!!.licence = stage.licence
        }
        if (recordTrack!!.schwierigkeit.toFloat() != binding.teDifficult.rating) {
            stage.schwierigkeit = binding.teDifficult.rating.toLong()
            changed = true
            recordTrack!!.schwierigkeit = stage.schwierigkeit
        }
        if (marker != null) {
            if (recordTrack!!.restId > 0) {
                if ((marker!!.position.latitude * fak6).roundToInt().toDouble() / fak6 != SecHelper.entcryptXtude(recordTrack!!.latitude)) {
                    stage.latitude = (marker!!.position.latitude * fak6).roundToInt().toDouble() / fak6
                    changed = true
                    recordTrack!!.latitude = SecHelper.cryptXtude(stage.latitude)
                }
                if ((marker!!.position.longitude * fak6).roundToInt().toDouble() / fak6 != SecHelper.entcryptXtude(recordTrack!!.longitude)) {
                    stage.longitude = (marker!!.position.longitude * fak6).roundToInt().toDouble() / fak6
                    changed = true
                    recordTrack!!.longitude = SecHelper.cryptXtude(stage.longitude)
                }
                stage.insDistance = recordTrack!!.distance2location
                if (googleApiClient!!.isConnected && permissionHelper.hasLocationPermission()) {
                    LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                        .addOnSuccessListener(requireActivity()) { lastKnown: Location? ->
                            if (lastKnown != null) {
                                stage.insLatitude = lastKnown.latitude
                                stage.insLongitude = lastKnown.longitude
                            }
                        }
                }
            } else { // new track
                stage.latitude = Math.round(marker!!.position.latitude * fak6).toDouble() / fak6
                stage.longitude = Math.round(marker!!.position.longitude * fak6).toDouble() / fak6
                changed = true
                if (googleApiClient!!.isConnected && permissionHelper.hasLocationPermission()) {
                    LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                        .addOnSuccessListener(requireActivity()) { lastKnown: Location? ->
                            if (lastKnown != null) {
                                stage.insLatitude = lastKnown.latitude
                                stage.insLongitude = lastKnown.longitude
                                val dest = Location("x")
                                dest.latitude = stage.latitude
                                dest.longitude = stage.longitude
                                stage.insDistance = Math.round(lastKnown.distanceTo(dest)).toLong()
                            }
                        }
                }
            }
        }
        if (changed && save) {
            if (isGoogleTests) {
                Toast.makeText(requireContext(), "you test the app, event is ignored", Toast.LENGTH_SHORT).show()
            } else {
                stage.save()
                recordTrack?.save()
                recordTrack = null // prepare resume, for not recycled
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.save_teackstage), Toast.LENGTH_LONG).show()
                doSync(updateProvider = true, force = true, flavor = BuildConfig.FLAVOR)
            }
        }
        return changed
    }

    private fun getValue(value: String?): String {
        return value ?: ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_track_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var res = super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menu_save) {
            if (mask2Record(true)) {
                requireActivity().finish()
            }
            res = true
        }
        return res
    }

    @SuppressLint("MissingPermission")
    private fun setUpMapIfNeeded() {
        val fmC = childFragmentManager
        var mapFragment = fmC.findFragmentById(R.id.map_edit) as FragmentMapScroll?
        if (mapFragment == null) {
            mapFragment = parentFragmentManager.findFragmentById(R.id.map_edit) as FragmentMapScroll?
        }
        if (map == null && mapFragment != null) {
            mapFragment.getMapAsync(OnMapReadyCallback { googleMap: GoogleMap ->
                map = googleMap
                map!!.mapType = GoogleMap.MAP_TYPE_HYBRID
                if (permissionHelper.hasLocationPermission()) {
                    map!!.isMyLocationEnabled = false
                }
                map!!.isTrafficEnabled = false
                map!!.uiSettings.isZoomControlsEnabled = true
                map!!.uiSettings.isMapToolbarEnabled = false
                map!!.setOnMarkerDragListener(this@BaseFragmentTrackEdit)
                map!!.setOnMarkerClickListener(this@BaseFragmentTrackEdit)
                map!!.setOnMapLoadedCallback { doAfterMapLoaded() }
                fillMask(id)
                setUpLocationClientIfNeeded()
            })
            id = 0
            if (requireActivity().intent.extras != null && requireActivity().intent.extras!!.containsKey(FragmentUpDown.RECORD_ID_LOCAL)) {
                id = requireActivity().intent.extras!!.getLong(FragmentUpDown.RECORD_ID_LOCAL)
            }
            fillMask(id)

            // Workaround, to require results
            mapFragment.setListener(object : FragmentMapScroll.OnTouchListener {
                override fun onTouch() {
                    binding.scrollviewEdit.requestDisallowInterceptTouchEvent(true)
                }
            })

        }
    }

    protected open fun doAfterMapLoaded() {}
    private fun addMarker(latlng: LatLng) {
        if (marker != null) {
            marker!!.remove()
        }
        if (map != null) {
            marker = map!!.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new_track))
                    .draggable(true)
            )
            val clickZlr = MxPreferences.getInstance().markerShowLongClickText
            if (clickZlr < MAX_MARKER_TEXT_SHOW) {
                marker?.title = requireActivity().getString(R.string.stage_longclick)
                MxPreferences.getInstance().edit().putMarkerShowLongClickText(clickZlr + 1).commit()
                marker?.showInfoWindow()
            }
            map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
        }
    }

    private fun setUpLocationClientIfNeeded() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        }
        googleApiClient!!.connect()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onMarkerDrag(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {}
    override fun onMarkerDragStart(marker: Marker) {
        if (activity != null) {
            val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(100)
            }
        }
    }

    override fun onConnectionFailed(result: ConnectionResult) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMoviesOperationExecutor = OperationExecutor(OP_GET_LAT_LNG, null, mOpeExCallbacks)
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(connectionHint: Bundle?) {
        // only on new track
        if (id < 1 && permissionHelper.hasLocationPermission()) { // new track
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation
                .addOnSuccessListener(requireActivity()) { lastKnown: Location? ->
                    if (this.isAdded && requireActivity().intent != null && requireActivity().intent.extras != null) {
                        if (requireActivity().intent.extras!!.containsKey(ActivityMapExtension.LAT)) {
                            val lat =
                                requireActivity().intent.extras!!.getDouble(ActivityMapExtension.LAT)
                            val lon =
                                requireActivity().intent.extras!!.getDouble(ActivityMapExtension.LON)
                            val interestLatLng = LatLng(lat, lon)
                            addMarker(interestLatLng)
                        }
                    } else if (lastKnown != null) {
                        val locLatLng = LatLng(lastKnown.latitude, lastKnown.longitude)
                        addMarker(locLatLng)
                    } else {
                        val country = Locale.getDefault().displayCountry
                        getMoviesOperationExecutor!!.execute(
                            AbstractOpGetLatLngOperation.newIntent(country),
                            OperationExecutor.MODE_ONCE
                        )
                        askOpenLocationSetting()
                    }
                }
            startLocationUpdates()
        }
    }

    private fun askOpenLocationSetting() {
        try {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.locationservices)
            builder.setMessage(R.string.ask_locationservices)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                    val gpsOptionsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(gpsOptionsIntent)
                }
                .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.show()
        } catch (ignored: NullPointerException) {
        }
    }

    private val mOpeExCallbacks: OperationExecutorCallbacks = object : OperationExecutorCallbacks {
        override fun onOperationPending(key: String) {}
        override fun onOperationComplete(key: String, result: OperationResult): Boolean {
            if (!isAdded) {
                return false
            }
            if (OP_GET_LAT_LNG == key && result.data != null) {
                if (result.data.containsKey(OpGetLatLngOperation.LAT)) {
                    addMarker(LatLng(result.data.getDouble(OpGetLatLngOperation.LAT), result.data.getDouble(OpGetLatLngOperation.LNG)))
                }
                return true
            }
            return false
        }
    }

    override fun onConnectionSuspended(arg0: Int) {}

    companion object {
        private const val OP_GET_LAT_LNG = "OP_GET_LAT_LNG"
        private const val MAX_MARKER_TEXT_SHOW = 4
        private const val fak6 = 1000000
        private const val fak5 = 100000
        private const val DEFAULT_LAT = 51.16569
        private const val DEFAULT_LON = 10.45153
        private fun getDisplaySize(display: Display): Point {
            val point = Point()
            display.getSize(point)
            return point
        }
    }
}