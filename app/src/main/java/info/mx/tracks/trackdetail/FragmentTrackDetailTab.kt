package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.common.BitmapHelper
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.On3StateClickListener
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.databinding.ContentFragmentDetailTabsIndicatorBinding
import info.mx.tracks.ops.AbstractOpGetWeatherCachedOperation
import info.mx.tracks.ops.AbstractOpPostTrackAppovedOperation
import info.mx.tracks.ops.google.PictureIdlingResource
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksgesRecord
import info.mx.tracks.util.ZoomOutPageTransformer
import info.mx.tracks.util.getDrawableIdentifier
import timber.log.Timber
import java.util.Locale
import kotlin.math.roundToInt

class FragmentTrackDetailTab : FragmentUpDown(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var adapterFragmentsTab: AdapterFragmentsTab
    private var adapterImages: ImageCursorAdapter? = null
    private var oldPictureCount = 0

    private val fragmentTrackDetail: FragmentTrackDetail?
        get() {
            var fragmentTrackDetail: FragmentTrackDetail? = null
            for (i in 0 until adapterFragmentsTab.count) {
                val fragment = findPagerFragmentByPosition(i)
                if (fragment != null) {
                    if (fragment is FragmentTrackDetail) {
                        fragmentTrackDetail = fragment
                        break
                    }
                }
            }
            return fragmentTrackDetail
        }

    private var _binding: ContentFragmentDetailTabsIndicatorBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ContentFragmentDetailTabsIndicatorBinding.inflate(inflater, container, false)
        val view = binding.root

        adapterFragmentsTab = AdapterFragmentsTab(requireActivity(), childFragmentManager, requireArguments())

        binding.viewPager.adapter = adapterFragmentsTab
        binding.viewPager.currentItem = MxPreferences.getInstance().tabDetailPosition
        binding.viewPager.setPageTransformer(true, ZoomOutPageTransformer())
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageSelected(position: Int) {
                MxPreferences.getInstance().edit().putTabDetailPosition(position).apply()
            }

        })


        if (requireArguments().containsKey(FragmentTrackDetail.IN_SLIDER) &&
            requireArguments().getBoolean(FragmentTrackDetail.IN_SLIDER)
        ) {
            view.findViewById<View>(R.id.tr_gen_layoutTrackname).visibility = View.GONE
        }

        val trackRec = SQuery.newQuery()
            .expr(MxInfoDBContract.Tracksges._ID, SQuery.Op.EQ, requireArguments().getLong(RECORD_ID_LOCAL))
            .selectFirst<TracksgesRecord>(MxInfoDBContract.Tracksges.CONTENT_URI)
        if (trackRec != null) {
            fillNextPrevId(trackRec.id)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        adapterFragmentsTab.notifyDataSetChanged()
        if (requireArguments().containsKey(RECORD_ID_LOCAL)) {
            recordLocalId = requireArguments().getLong(RECORD_ID_LOCAL)
            fillMask(recordLocalId)
        }
    }

    override fun fillMask(localId: Long) {
        val bundle = Bundle()
        bundle.putLong(RECORD_ID_LOCAL, localId)
        loaderManager.restartLoader(LOADER_TRACK, bundle, this)

        for (i in 0 until adapterFragmentsTab.count) {
            val fragment = findPagerFragmentByPosition(i)
            if (fragment != null) {
                // Tablets have a FragmentTrackList
                if (fragment is FragmentUpDown) {
                    fragment.fillMask(localId)
                }
            } else {
                Timber.d("fragment not found $i/${adapterFragmentsTab.count} null")
            }
        }
    }

    private fun findPagerFragmentByPosition(position: Int): Fragment? {
        var result = parentFragmentManager.findFragmentByTag("android:switcher:" + binding.viewPager.id + ":" + adapterFragmentsTab.getItemId(position))
        if (result == null) {
            result = childFragmentManager.findFragmentByTag("android:switcher:" + binding.viewPager.id + ":" + adapterFragmentsTab.getItemId(position))
        }
        return result
    }

    @SuppressLint("DiscouragedApi")
    private fun fillMask(trackRec: TracksgesRecord) {
        trackRec.trackname?.let {
            MxCoreApplication.trackEvent("TrackOpen", it)
        }

        Ops.execute(AbstractOpGetWeatherCachedOperation.newIntent(trackRec.id))

        if (trackRec.country != null && activity != null) {
            val idCountryImg = resources
                .getDrawableIdentifier(trackRec.country.lowercase(Locale.getDefault()) + "2x", requireActivity().packageName)
            binding.trGenDetailCountry.setImageResource(idCountryImg)
        }
        binding.trGenDetailName.text = trackRec.trackname

        binding.trGenApproved.visibility = if (MxCoreApplication.isAdmin && trackRec.restId > 0) View.VISIBLE else View.GONE
        binding.trGenApproved.setImageLevel((trackRec.approved + 1).toInt())
        binding.trGenApproved.tag = trackRec.restId
        binding.trGenApproved.setOnClickListener(object : On3StateClickListener() {

            override fun onClick(view: View) {
                super.onClick(view)
                val myiId = view.tag as Long
                val intent = AbstractOpPostTrackAppovedOperation.newIntent(myiId, level - 1)
                Ops.execute(intent)
            }
        })

        binding.trGenDetailArt.tag = trackRec.trackaccess
        binding.trGenDetailArt.setOnClickListener { v ->
            var text = requireActivity().getString(R.string.open_everyone)
            when (v.tag as String) {
                "R" -> text = requireActivity().getString(R.string.open_race)
                "M" -> text = requireActivity().getString(R.string.open_member)
                "D" -> text = requireActivity().getString(R.string.open_dealer)
            }
            showMessage(text)
        }
        val iconRes: Int = when (trackRec.trackaccess) {
            "R" -> R.drawable.flag_race_hell
            "M" -> R.drawable.flag_member_hell
            "D" -> R.drawable.flag_dealer_large2x
            else -> R.drawable.flag_blau_hell
        }
        binding.trGenDetailArt.setImageBitmap(BitmapHelper.getBitmap(requireActivity(), iconRes, trackRec.trackstatus))
        Timber.i("fillMask track:%s", trackRec.id)

        adapterImages = ImageCursorAdapter(requireActivity(), null, resources.getDimension(R.dimen.thumbnail_size_dp).roundToInt(), true)
        val layoutRecycler = LinearLayoutManager(requireActivity())
        layoutRecycler.orientation = LinearLayoutManager.HORIZONTAL
        binding.hImgGalleryGen.layoutManager = layoutRecycler
        binding.hImgGalleryGen.adapter = adapterImages

        val layoutRecyclerW = LinearLayoutManager(activity)
        layoutRecyclerW.orientation = LinearLayoutManager.HORIZONTAL

        fillNextPrevId(trackRec.id)
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        val query: SQuery
        return when (loader) {
            LOADER_TRACK -> SQuery.newQuery().expr(MxInfoDBContract.Tracksges._ID, SQuery.Op.EQ, bundle!!.getLong(RECORD_ID_LOCAL))
                .createSupportLoader(MxInfoDBContract.Tracksges.CONTENT_URI, null, null)

            //LOADER_PICTURE_THUMBS,
            else -> {
                query = QueryHelper.getPictureFilter(bundle!!.getLong(FragmentTrackDetail.TRACK_REST_ID))
                query.createSupportLoader(MxInfoDBContract.Pictures.CONTENT_URI, null, MxInfoDBContract.Pictures.REST_ID)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_TRACK -> {
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                if (cursor.count == 1) {
                    val recordTrack = TracksgesRecord.fromCursor(cursor)
                    recordTrack?.let {
                        fillMask(it)
                    } ?: run {
                        Timber.e(Throwable("recordTrack is null in onLoadFinished ${cursor.count} $cursor"))
                    }
                    val bundle = Bundle()
                    bundle.putLong(FragmentTrackDetail.TRACK_REST_ID, recordTrack.restId)
                    bundle.putLong(RECORD_ID_LOCAL, recordTrack.id)
                    loaderManager.restartLoader(LOADER_PICTURE_THUMBS, bundle, this)
                }
            }

            LOADER_PICTURE_THUMBS -> {
                adapterImages!!.swapCursor(cursor)
                if (oldPictureCount != cursor.count)
                    repeat((0..<cursor.count).count()) {
                        PictureIdlingResource.increment(it, cursor.count)
                    }
                oldPictureCount = cursor.count
                binding.trLayoutUploadHorizontalGen.visibility = if (cursor.count == 0) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> adapterImages!!.swapCursor(null)
        }
    }

    private fun showMessage(text: String) {
        if (context is ActivityTrackDetail) {
            (context as ActivityTrackDetail).showMessage(text, Snackbar.LENGTH_SHORT)
        }
    }

    fun addEvent() {
        fragmentTrackDetail?.addEvent()
    }

    fun doPicturePick() {
        fragmentTrackDetail?.doPicturePick()
    }

    fun addRating() {
        fragmentTrackDetail?.addRating()
    }

    companion object {

        const val TAG = "FragmentTrackDetailTab"
        private const val LOADER_TRACK = 1
        private const val LOADER_PICTURE_THUMBS = 2
    }
}
