package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import info.hannes.commonlib.LocationHelper
import info.mx.tracks.R
import info.mx.tracks.adapter.AdapterBitmaps
import info.mx.tracks.databinding.FragmentPlaceDetailBinding
import info.mx.tracks.map.MxPlace
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentPlaceDetail : Fragment(), MxPlace.PhotoReadyCallBack {

    private var mxPlace: MxPlace? = null
    private lateinit var photoAdapter: AdapterBitmaps
    val scrollContent: NestedScrollView?
        get() = _binding?.scrollContent

    private var _binding: FragmentPlaceDetailBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentPlaceDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.poiDetailPhone.setOnTouchListener(object : FeedBackTouchListener() {

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var res = false // super.onTouch(view, motionEvent);
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> res = true
                    MotionEvent.ACTION_CANCEL -> res = true
                    MotionEvent.ACTION_UP -> {
                        res = true
                        if ((view as TextView).text.toString().contains("\n")) {
                            doSelectDlg(requireActivity(), R.string.phone, view.text.toString())
                        } else {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + view.text.toString())
                            startActivity(intent)
                        }
                    }
                }
                return res
            }
        })

        binding.poiDetailWebsite.setOnTouchListener(object : FeedBackTouchListener() {

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var res = false // super.onTouch(view, motionEvent);
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> res = true
                    MotionEvent.ACTION_CANCEL -> res = true
                    MotionEvent.ACTION_UP -> {
                        res = true
                        openWebSite(requireActivity(), (view as TextView).text.toString())
                    }
                }
                return res
            }
        })

        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mxPlace = arguments?.getParcelable(PLACE)
        mxPlace?.let {
            fillMask(it)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.menu_image_upload, menu)
    }

    @SuppressLint("NewApi")
    open inner class FeedBackTouchListener : View.OnTouchListener {
        private var saveColor: Drawable? = null

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    saveColor = view.background
                    context?.let {
                        view.setBackgroundColor(ContextCompat.getColor(it, R.color.dark_blue))
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> view.background = saveColor
            }
            return false
        }
    }

    private fun fillMask(mxPlace: MxPlace) {

        binding.poiLayoutRating.visibility = if (mxPlace.rating < 0) View.GONE else View.VISIBLE
        binding.poiRating.rating = mxPlace.rating

        if (mxPlace.phoneNumber == null || mxPlace.phoneNumber == "") {
            binding.poiLayoutphone.visibility = LinearLayout.GONE
        } else {
            binding.poiLayoutphone.visibility = LinearLayout.VISIBLE
            binding.poiDetailPhone.text = mxPlace.phoneNumber
        }

        if (mxPlace.websiteUri == null || mxPlace.websiteUri.toString() == "") {
            binding.poiLayoutwebsite.visibility = LinearLayout.GONE
        } else {
            binding.poiLayoutwebsite.visibility = LinearLayout.VISIBLE
            binding.poiDetailWebsite.text = mxPlace.websiteUri.toString()
        }

        binding.poiDetailAddress.text = mxPlace.address.toString().replace(", ", "\n")
        binding.poiHImgGalery.visibility = if (mxPlace.photoList.size == 0) View.GONE else View.VISIBLE

        val layoutRecycler = LinearLayoutManager(activity)
        layoutRecycler.orientation = LinearLayoutManager.HORIZONTAL
        binding.poiHImgGalery.layoutManager = layoutRecycler

        photoAdapter = AdapterBitmaps(mxPlace.photoList)
        binding.poiHImgGalery.adapter = photoAdapter

        mxPlace.setOnPhotoReadyCallBack(this)

        val height = requireActivity().resources.getDimension(R.dimen.thumbnail_size_dp).roundToInt()

        viewLifecycleOwner.lifecycleScope.launch {
            mxPlace.getPhotos(Places.createClient(requireContext()), height * 4 / 3, height)
        }
    }

    override fun onPhotoReceived(photoList: List<Bitmap>) {
        photoAdapter = AdapterBitmaps(photoList)
        binding.poiHImgGalery.adapter = photoAdapter
        binding.poiHImgGalery.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_place_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var res = super.onOptionsItemSelected(item)

        if (item.itemId == R.id.menu_place_navigation) {
            doOpenNavigation(mxPlace!!)
            res = true
        } else if (item.itemId == R.id.menu_place_share) {
            doShare(mxPlace!!)
            res = false
        }
        return res
    }

    private fun doShare(mxPlace: MxPlace) {
        mxPlace.latLng?.let {
            val message = mxPlace.name.toString() + "\n" +
                    mxPlace.address +
                    "\nhttps://www.google.de/maps/@" + it.latitude + "," +
                    it.longitude +
                    ",11z"
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(share, requireContext().getText(R.string.share)))
        }
    }

    private fun doOpenNavigation(mxPlace: MxPlace) {
        mxPlace.latLng?.let {
            context?.let { ctx ->
                LocationHelper.openNavi(ctx, mxPlace.name.toString(), it.latitude, it.longitude)
            }
        }
    }

    companion object {

        const val PLACE = "place"

        fun openWebSite(activity: Activity, urls: String) {
            if (urls.contains("\n")) {
                doSelectDlg(activity, R.string.website, urls)
            } else {
                var url = urls
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://$url"
                }
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                activity.startActivity(i)
            }
        }

        fun doSelectDlg(activity: Activity, kind: Int, gesString: String) {
            val alertChoice: Dialog
            val sortItems = gesString.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val builder = AlertDialog.Builder(activity)
            // telephone get an icon
            if (kind == R.string.phone) {
                val inflater = activity.layoutInflater
                @SuppressLint("InflateParams") val viewHeader = inflater.inflate(R.layout.dialog_header_image, null)
                (viewHeader.findViewById<View>(R.id.dialogheader_button) as ImageView).setImageResource(android.R.drawable.ic_menu_call)
                (viewHeader.findViewById<View>(R.id.dialogheader_text) as TextView).text = activity.getString(kind)
                builder.setCustomTitle(viewHeader)
            } else {
                builder.setTitle(activity.getString(kind))
            }
            builder.setItems(sortItems) { _, item ->
                if (kind == R.string.phone) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + sortItems[item])
                    activity.startActivity(intent)
                } else if (kind == R.string.website) {
                    var url = sortItems[item]
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$url"
                    }
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    activity.startActivity(i)
                }
            }
            alertChoice = builder.create()
            alertChoice.show()
        }
    }

}
