package info.mx.tracks.stage

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.DateHelper.shortWeekdays
import info.mx.commonlib.LocationHelper.getFormatDistance
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.tracks.BuildConfig
import info.mx.core.MxCoreApplication.Companion.isAdmin
import info.mx.tracks.R
import info.mx.tracks.base.FragmentBase
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.StatusHelper
import info.mx.tracks.common.getStageValues
import info.mx.tracks.databinding.FragmentTrackDetailBinding
import info.mx.core_generated.ops.AbstractOpSyncFromServerOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.MxInfoDBContract.Trackstage
import info.mx.core_generated.sqlite.TrackstageRecord
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class FragmentStageNewDetail : FragmentBase(), LoaderManager.LoaderCallbacks<Cursor> {

    private val dataManagerAdmin: DataManagerAdmin by inject()

    private var _binding: FragmentTrackDetailBinding? = null
    internal val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val shortWeekdays = shortWeekdays

        binding.textRatingCount.visibility = View.GONE
        binding.lyWeatherHorizontal.visibility = View.GONE
        binding.trLayoutBrands.visibility = View.GONE
        binding.trLayoutRating.visibility = View.GONE
        binding.trLayoutStage.visibility = View.VISIBLE
        binding.trDetailEventlogo.visibility = View.GONE
        binding.dayMo.dayName.text = shortWeekdays[2]
        binding.dayDi.dayName.text = shortWeekdays[3]
        binding.dayMi.dayName.text = shortWeekdays[4]
        binding.dayDo.dayName.text = shortWeekdays[5]
        binding.dayFr.dayName.text = shortWeekdays[6]
        binding.daySa.dayName.text = shortWeekdays[7]
        binding.daySo.dayName.text = shortWeekdays[1]
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup menu using MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_stage_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return handleMenuItemSelected(menuItem)
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        if (arguments != null && requireArguments().containsKey(FragmentUpDown.RECORD_ID_LOCAL)) {
            val id = requireArguments().getLong(FragmentUpDown.RECORD_ID_LOCAL)
            fillMask(id)
        }
    }

    @SuppressLint("NewApi")
    private fun setDateField(view: TextView, value: Long) {
        if (value == 0L || value == -1L) {
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_edges_grey)
            TextViewCompat.setTextAppearance(view, R.style.textAppearanceSmall)
        } else {
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_edges_blue)
        }
        view.setTextColor(Color.WHITE)
    }

    fun fillMask(trackId: Long) {
        val bundle = Bundle()
        bundle.putLong(TRACK_CLIENT_ID, trackId)
        loaderManager.restartLoader(LOADER_TRACK, bundle, this)
    }

    @SuppressLint("SetTextI18n")
    private fun fillMask(trackRec: TrackstageRecord) {
        val trackAccess = if (trackRec.trackaccess == null) "" else trackRec.trackaccess
        binding.trDetailDifficult.rating = trackRec.schwierigkeit.toFloat()
        binding.distanceContainer.trDetailDistance.text = getFormatDistance(
            MxPreferences.getInstance().unitsKm, trackRec.insDistance
                .toInt()
        )
        binding.trLayoutDifficult.visibility = if (trackRec.schwierigkeit == 0L) View.GONE else View.VISIBLE
        val hideTimes = trackRec.hoursmonday == null &&
                trackRec.hourstuesday == null &&
                trackRec.hourswednesday == null &&
                trackRec.hoursthursday == null &&
                trackRec.hoursfriday == null &&
                trackRec.hourssaturday == null &&
                trackRec.hourssunday == null &&
                trackRec.openmondays == 0L &&
                trackRec.opentuesdays == 0L &&
                trackRec.openwednesday == 0L &&
                trackRec.openthursday == 0L &&
                trackRec.openfriday == 0L &&
                trackRec.opensaturday == 0L &&
                trackRec.opensunday == 0L
        binding.trLayoutOpening.visibility = if (hideTimes) View.GONE else View.VISIBLE
        binding.trLayoutInfo.visibility =
            if (trackRec.mxTrack == 1L ||
                trackRec.quad == 1L ||
                trackRec.a4X4 == 1L ||
                trackRec.enduro == 1L ||
                trackRec.utv == 1L ||
                trackRec.facebook != null &&
                trackRec.facebook != ""
            )
                View.VISIBLE
            else
                View.GONE
        binding.imgFacebook.visibility =
            if (trackRec.facebook != null && trackRec.facebook != "") View.VISIBLE else View.GONE
        binding.imgMx.visibility = if (trackRec.mxTrack == 1L) View.VISIBLE else View.GONE
        binding.imgQuad.visibility = if (trackRec.quad == 1L) View.VISIBLE else View.GONE
        binding.img4x4.visibility = if (trackRec.a4X4 == 1L) View.VISIBLE else View.GONE
        binding.imgEnduro.visibility = if (trackRec.enduro == 1L) View.VISIBLE else View.GONE
        binding.imgUtv.visibility = if (trackRec.utv == 1L) View.VISIBLE else View.GONE
        setDateField(binding.dayMo.dayName, trackRec.openmondays)
        setDateField(binding.dayDi.dayName, trackRec.opentuesdays)
        setDateField(binding.dayMi.dayName, trackRec.openwednesday)
        setDateField(binding.dayDo.dayName, trackRec.openthursday)
        setDateField(binding.dayFr.dayName, trackRec.openfriday)
        setDateField(binding.daySa.dayName, trackRec.opensaturday)
        setDateField(binding.daySo.dayName, trackRec.opensunday)
        binding.dayMo.dayText.text = trackRec.hoursmonday
        binding.dayDi.dayText.text = trackRec.hourstuesday
        binding.dayMi.dayText.text = trackRec.hourswednesday
        binding.dayDo.dayText.text = trackRec.hoursthursday
        binding.dayFr.dayText.text = trackRec.hoursfriday
        binding.daySa.dayText.text = trackRec.hourssaturday
        binding.daySo.dayText.text = trackRec.hourssunday
        binding.trDetailWebsite.text = SecHelper.decryptB64(trackRec.url)
        binding.trDetailPhone.text = SecHelper.decryptB64(trackRec.phone)
        binding.trDetailContact.text = SecHelper.decryptB64(trackRec.contact)
        binding.trDetailNoise.text = trackRec.noiselimit
        binding.trDetailStatus.text = StatusHelper.getLongStatusText(requireActivity(), trackRec.trackstatus)
        binding.trDetailCamping.text = getString(if (trackRec.camping == 1L) R.string.yes else R.string.no)
        binding.trLayoutNoiseLimit.visibility = if (trackRec.noiselimit == null || trackRec.noiselimit == "") View.GONE else View.VISIBLE
        binding.trLayoutCamping.visibility = if (trackAccess == "D" || trackRec.camping == 0L) View.GONE else View.VISIBLE
        binding.trDetailShower.text = getString(if (trackRec.shower == 1L) R.string.yes else R.string.no)
        binding.trLayoutShower.visibility = if (trackAccess == "D" || trackRec.shower == 0L) View.GONE else View.VISIBLE
        binding.trDetailElectricity.text = getString(if (trackRec.electricity == 1L) R.string.yes else R.string.no)
        binding.trLayoutElect.visibility = if (trackAccess == "D" || trackRec.electricity == 0L) View.GONE else View.VISIBLE
        binding.trDetailBikecleaning.text = getString(if (trackRec.cleaning == 1L) R.string.yes else R.string.no)
        binding.trLayoutBikclean.visibility = if (trackAccess == "D" || trackRec.cleaning == 0L) View.GONE else View.VISIBLE
        binding.trDetailKids.text = getString(if (trackRec.kidstrack == 1L) R.string.yes else R.string.no)
        binding.trLayoutKids.visibility = if (trackAccess == "D" || trackRec.kidstrack == 0L) View.GONE else View.VISIBLE
        binding.trDetailNotes.text = trackRec.notes
        binding.trLayoutComment.visibility = if (trackRec.notes == null) View.GONE else View.VISIBLE
        if (trackRec.fees == null || trackRec.fees == "") {
            binding.trLayoutFees.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutFees.visibility = LinearLayout.VISIBLE
            binding.trDetailFees.text = trackRec.fees
        }
        if (trackRec.tracklength == 0L) {
            binding.trLayoutLength.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutLength.visibility = LinearLayout.VISIBLE
            binding.trDetailLength.text = java.lang.Long.toString(trackRec.tracklength)
        }
        if (trackRec.phone == null || trackRec.phone == "") {
            binding.trLayoutphone.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutphone.visibility = LinearLayout.VISIBLE
        }
        if (trackRec.contact == null || SecHelper.decryptB64(trackRec.contact) == "") {
            binding.trLayoutcontact.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutcontact.visibility = LinearLayout.VISIBLE
        }
        if (trackRec.url == null || SecHelper.decryptB64(trackRec.url) == "") {
            binding.trLayoutwebsite.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutwebsite.visibility = LinearLayout.VISIBLE
        }
        if (trackRec.soiltype == 0L) {
            binding.trLayoutSoil.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutSoil.visibility = LinearLayout.VISIBLE
            val soils = resources.getStringArray(R.array.soil_list)
            binding.trDetailSoil.text = ""
            try {
                binding.trDetailSoil.text = soils[trackRec.soiltype.toInt()]
            } catch (_: Exception) {
            }
        }
        if (trackAccess != "D") {
            binding.trLayoutAdress.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutAdress.visibility = LinearLayout.VISIBLE
            binding.trDetailAdress.text = SecHelper.decryptB64(trackRec.adress)
        }
        if (trackAccess != "D") {
            binding.trLayoutWorkshop.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutWorkshop.visibility = LinearLayout.VISIBLE
            binding.trDetailWorkshop.text =
                getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        }
        if (trackAccess != "D") {
            binding.trLayoutShowroom.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutShowroom.visibility = LinearLayout.VISIBLE
            binding.trDetailShowroom.text =
                getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        }
        binding.trLayoutCoordinates.visibility = if (isAdmin) LinearLayout.VISIBLE else LinearLayout.GONE
        binding.trDetailCoordinates.text = trackRec.latitude.toString() + " " + trackRec.longitude
        binding.trDetailLicense.text = trackRec.licence
        binding.trLayoutLicense.visibility =
            if (trackAccess == "D" || trackRec.licence == null || trackRec.licence == "") View.GONE else View.VISIBLE
        binding.trDetailSx.text = getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        binding.trLayoutSx.visibility =
            if (trackAccess == "D" || trackRec.supercross == 0L) View.GONE else View.VISIBLE
    }

    private fun handleMenuItemSelected(item: MenuItem): Boolean {
        var res = false
        val id = requireArguments().getLong(FragmentUpDown.RECORD_ID_LOCAL)
        val stage = TrackstageRecord.get(id)
        if (stage != null) {
            if (item.itemId == R.id.menu_stage_accept) {
                addDisposable(
                    dataManagerAdmin.trackStageAccept(stage.restId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally { Timber.d("()") }
                        .subscribe({
                            val intentM: Intent = AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                            Ops.execute(intentM)
                        }
                        ) { e: Throwable? -> Timber.e(e) }
                )
                res = true
            } else if (item.itemId == R.id.menu_stage_ignore) {
                addDisposable(
                    dataManagerAdmin.trackStageDelete(stage.restId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally { Timber.d("()") }
                        .subscribe({
                            val intentM: Intent = AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                            Ops.execute(intentM)
                        }
                        ) { e: Throwable? -> Timber.e(e) }
                )
                res = true
            } else if (item.itemId == R.id.menu_stage_decline) {
                addDisposable(
                    dataManagerAdmin.trackStageDecline(stage.restId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally { Timber.d("()") }
                        .subscribe({
                            val intentM: Intent = AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                            Ops.execute(intentM)
                        }
                        ) { e: Throwable? -> Timber.e(e) }
                )
                res = false
            }
        }
        return res
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        return SQuery.newQuery()
            .expr(Trackstage._ID, SQuery.Op.EQ, bundle!!.getLong(TRACK_CLIENT_ID))
            .createSupportLoader(Trackstage.CONTENT_URI, null, null)

    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_TRACK -> {
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                if (cursor.count == 1) {
                    val recordStage = TrackstageRecord.fromCursor(cursor)
                    fillMask(recordStage)
                    val txt: Spanned =
                        Html.fromHtml(
                            cursor.getStageValues(),
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    binding.trDetailStage.text = txt
                    val bundle = Bundle()
                    bundle.putLong(TRACK_REST_ID, recordStage.restId)
                    bundle.putLong(TRACK_CLIENT_ID, recordStage.id)
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}

    companion object {
        private const val TRACK_CLIENT_ID = "track_client_id"
        private const val TRACK_REST_ID = "track_rest_id"
        private const val LOADER_TRACK = 2
    }
}
