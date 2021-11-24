package info.mx.tracks.trackdetail

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.StaleDataException
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.Html
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Lifecycle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.db.SQuery.Op
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.DateHelper
import info.hannes.commonlib.utils.FileHelper
import info.mx.commonlib.LocationHelper
import info.mx.core.MxCoreApplication
import info.mx.core.common.DistanceHelper
import info.mx.core.sqlite.TracksDistanceRecord
import info.mx.core.tasks.ImportTaskCompleteListener
import info.mx.core_generated.ops.AbstractOpGetWeatherCachedOperation
import info.mx.core_generated.ops.AbstractOpPostImagesOperation
import info.mx.core_generated.ops.google.AbstractOpGetRouteOperation
import info.mx.core_generated.prefs.MxPreferences
import info.mx.core_generated.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.core_generated.sqlite.FavoritsRecord
import info.mx.core_generated.sqlite.MxInfoDBContract.*
import info.mx.core_generated.sqlite.PicturesRecord
import info.mx.core_generated.sqlite.RouteRecord
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.TracksgesRecord
import info.mx.core_generated.sqlite.WeatherRecord
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.common.SecHelper
import info.mx.tracks.common.StatusHelper
import info.mx.tracks.databinding.FragmentTrackDetailBinding
import info.mx.tracks.databinding.LayoutCommentAddBinding
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.rest.AppApiClient
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.room.entity.Comment
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.service.RecalculateDistance
import info.mx.tracks.trackdetail.comment.CommentViewModel
import info.mx.tracks.tracklist.FragmentTrackList.Companion.IS_FAVORITE
import info.mx.tracks.util.EventHelper
import info.mx.tracks.util.getAndroidID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


class FragmentTrackDetail : FragmentUpDown(), ImportTaskCompleteListener<String>, LoaderManager.LoaderCallbacks<Cursor>, LocationListener {

    private val mxDatabase: MxDatabase by inject()

    private val appApiClient: AppApiClient by inject()

    private val commentViewModel: CommentViewModel by viewModel()

    private var adapterWeather: WeatherCursorAdapter? = null
    private var recordTrack: TracksgesRecord? = null
    private var fileAbsolute: String? = null
    private var recordTrackDist: TracksDistanceRecord? = null
    private var currPosition: Int = 0

    // Modern Activity Result API launchers
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentTrackDetailBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register for photo capture result
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handlePhotoResult(result.data)
            }
        }

        // Register for photo pick result
        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handlePhotoResult(result.data)
            }
        }

        // Register for storage permission result
        requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                pickPhotoLauncher.launch(photoPickerIntent)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrackDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        fileAbsolute = MxCoreApplication.getFilesDir(requireActivity()) + File.separatorChar + PREFIX_IMAGE_NAME +
                System.currentTimeMillis() + IMAGE_EXTENSION

        val shortWeekdays = DateHelper.shortWeekdays

        binding.dayMo.dayName.text = shortWeekdays[2]
        binding.dayDi.dayName.text = shortWeekdays[3]
        binding.dayMo.dayName.text = shortWeekdays[4]
        binding.dayDo.dayName.text = shortWeekdays[5]
        binding.dayFr.dayName.text = shortWeekdays[6]
        binding.daySa.dayName.text = shortWeekdays[7]
        binding.daySo.dayName.text = shortWeekdays[1]

        binding.trDetailPhone.setOnTouchListener(object : FeedBackTouchListener() {

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
                            intent.data = ("tel:" + view.text.toString()).toUri()
                            startActivity(intent)
                        }
                    }
                }
                return res
            }
        })

        binding.trDetailContact.setOnTouchListener(object : FeedBackTouchListener() {

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var res = false // super.onTouch(view, motionEvent);
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> res = true

                    MotionEvent.ACTION_CANCEL -> res = true

                    MotionEvent.ACTION_UP -> {
                        res = true
                        doSendMail((view as TextView).text.toString())
                    }
                }
                return res
            }
        })

        binding.trDetailCoordinates.setOnTouchListener(object : FeedBackTouchListener() {

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var res = false // super.onTouch(view, motionEvent);
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> res = true

                    MotionEvent.ACTION_CANCEL -> res = true

                    MotionEvent.ACTION_UP -> {
                        val loc = view.tag as Location
                        doOpenMap(loc)
                        res = true
                    }
                }
                return res
            }
        })

        if (permissionHelper.hasLocationPermission()) {
            (binding.distanceContainer.trDetailDistance.parent.parent as ViewGroup).visibility = View.VISIBLE
        } else {
            (binding.distanceContainer.trDetailDistance.parent.parent as ViewGroup).visibility = View.GONE
        }
        binding.distanceContainer.trDetailDistance.setOnClickListener { this@FragmentTrackDetail.doOpenNavigation() }
        binding.trDetailDistanceNavigate.setOnTouchListener(object : FeedBackTouchListener() {

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                var res = false // super.onTouch(view, motionEvent);
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> res = true

                    MotionEvent.ACTION_CANCEL -> res = true

                    MotionEvent.ACTION_UP -> {
                        doOpenNavigation()
                        res = true
                    }
                }
                return res
            }
        })

        binding.trDetailWebsite.setOnTouchListener(object : FeedBackTouchListener() {

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

        if (requireArguments().containsKey(IN_SLIDER) && requireArguments().getBoolean(IN_SLIDER)) {
            binding.trLayoutDistance.visibility = View.GONE
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(CURSOR_POSITION, currPosition)
        savedInstanceState.putString(CAPTURED_FILENAME, fileAbsolute)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup menu using MenuProvider
        setupMenu()

        // Restore saved state
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CAPTURED_FILENAME)) {
                fileAbsolute = savedInstanceState.getString(CAPTURED_FILENAME)
            }
            if (savedInstanceState.containsKey(CURSOR_POSITION)) {
                currPosition = savedInstanceState.getInt(CURSOR_POSITION)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireArguments().containsKey(RECORD_ID_LOCAL)) {
            recordLocalId = requireArguments().getLong(RECORD_ID_LOCAL)
            fillMask(recordLocalId)

            if (requireArguments().containsKey(RecalculateDistance.EDIT)) {
                val trackRecord = TracksgesRecord.get(recordLocalId)
                if (trackRecord != null) {
                    MxCoreApplication
                        .trackEvent("check " + " " + requireArguments().getBoolean(RecalculateDistance.EDIT), trackRecord.trackname)
                }
                if (requireArguments().getBoolean(RecalculateDistance.EDIT)) {
                    openEdit(recordLocalId)
                }
                requireArguments().remove(RecalculateDistance.EDIT)
            }
            // get poi with loader
            // Bundle bundle = new Bundle();
            // bundle.putLong(TRACK_ID, getArguments().getLong(TRACK_ID));
            // getLoaderManager().initLoader(LOADER_TRACK, bundle, this);
        }
        setUpLocationClientIfNeeded()
        startLocationUpdates()
    }

    override fun onPause() {
        stopLocationUpdates()
        super.onPause()
    }

    override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, view, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.menu_image_upload, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_image_pick -> {
                doPicturePick()
                true
            }

            R.id.menu_image_make -> {
                doPictureMake()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    @SuppressLint("NewApi")
    open inner class FeedBackTouchListener : View.OnTouchListener {
        private var saveColor: Drawable? = null

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    saveColor = view.background
                    view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_blue))
                }

                MotionEvent.ACTION_UP -> {
                    view.background = saveColor
                    view.performClick()
                }

                MotionEvent.ACTION_CANCEL -> view.background = saveColor
            }
            return false
        }
    }

    @SuppressLint("SdCardPath")
    private fun handlePhotoResult(imageReturnedIntent: Intent?) {
        if (requireArguments().containsKey(RECORD_ID_LOCAL)) {
            recordLocalId = requireArguments().getLong(RECORD_ID_LOCAL)
            if (recordTrack == null) {
                recordTrack = TracksgesRecord.get(recordLocalId)
            }
        }
        if (recordTrack == null) {
            requireActivity().finish()
            return
        }

        if (imageReturnedIntent == null) {
            showMessage(getString(R.string.nothing_selected), Snackbar.LENGTH_SHORT)
            return
        }

        // Handle make photo result
        if (imageReturnedIntent.hasExtra("photo_type") && imageReturnedIntent.getStringExtra("photo_type") == "take") {
            var uriMakePhoto: Uri? = null
            if (hasImageCaptureBug()) {
                val fi = File("/sdcard/tmp")
                try {
                    uriMakePhoto = insertImageToMediaStore(requireActivity().contentResolver, fi)
                } catch (e: FileNotFoundException) {
                    Timber.e(e)
                } catch (e: IOException) {
                    Timber.e(e)
                }
            } else {
                uriMakePhoto = imageReturnedIntent.data
            }

            if (uriMakePhoto != null) {
                fileAbsolute = uriMakePhoto.path
            }
            if (imageReturnedIntent.data != null) {
                fileAbsolute = imageReturnedIntent.data!!.path
            }
            val newFile = File(fileAbsolute!!)
            val bitmapData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                imageReturnedIntent.extras?.getParcelable(DATA, Bitmap::class.java)
            } else {
                @Suppress("DEPRECATION")
                imageReturnedIntent.extras?.getParcelable(DATA)
            }
            if (!newFile.exists() && bitmapData != null) {
                try {
                    Timber.i("bitmap.height = %s, bitmap width %s", bitmapData.height, bitmapData.width)
                    val fOut = FileOutputStream(fileAbsolute!!)
                    bitmapData.compress(Bitmap.CompressFormat.PNG, 85, fOut)
                    fOut.flush()
                    fOut.close()
                } catch (e: IOException) {
                    e.message?.let { showMessage(it, Snackbar.LENGTH_LONG) }
                    Timber.e(e)
                }
            }

            val orientation = requireActivity().requestedOrientation
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                Timber.w("rotate should be done")
            }

            val newImg = PicturesRecord()
            newImg.trackRestId = recordTrack!!.restId
            newImg.localfile = fileAbsolute
            newImg.save()

            val intentM = AbstractOpPostImagesOperation.newIntent()
            Ops.execute(intentM)
        } else {
            // Handle select photo result
            val uri = imageReturnedIntent.data

            if (uri != null) {
                var imageFilePath = ""
                val cursor = requireActivity().contentResolver
                    .query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    imageFilePath = cursor.getString(0)
                    cursor.close()
                }

                val sourceFile = File(imageFilePath)
                val localCopy = File(MxCoreApplication.getFilesDir(requireActivity()), sourceFile.name)
                try {
                    FileHelper.copyFile(sourceFile, localCopy)
                    val picRecord = PicturesRecord()
                    picRecord.localfile = localCopy.absolutePath
                    picRecord.username = MxPreferences.getInstance().username
                    picRecord.trackRestId = recordTrack!!.restId
                    picRecord.save()
                    val intentS = AbstractOpPostImagesOperation.newIntent()
                    Ops.execute(intentS)
                } catch (e: IOException) {
                    e.message?.let { showMessage(it, Snackbar.LENGTH_LONG) }
                    Timber.e("IOException %s", e.message)
                }
            }
        }
    }

    private fun doSendMail(contact: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(contact))
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (_: ActivityNotFoundException) {
            showMessage(getString(R.string.no_email_installed), Snackbar.LENGTH_SHORT)
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

    override fun fillMask(localId: Long) {
        recordLocalId = localId
        val bundle = Bundle()
        bundle.putLong(RECORD_ID_LOCAL, localId)
        loaderManager.restartLoader(LOADER_TRACK, bundle, this)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "MissingPermission")
    private fun fillMask(trackRec: TracksgesRecord?) {
        if (trackRec != null)
            MxCoreApplication.trackEvent("TrackOpen", trackRec.trackname)

        Ops.execute(AbstractOpGetWeatherCachedOperation.newIntent(trackRec!!.id))

        val layoutRecyclerW = LinearLayoutManager(requireActivity())
        layoutRecyclerW.orientation = LinearLayoutManager.HORIZONTAL
        adapterWeather = WeatherCursorAdapter(requireActivity(), null)
        binding.hWeatherGalery.layoutManager = layoutRecyclerW
        binding.hWeatherGalery.adapter = adapterWeather
        // in CI we don't show weather.
        // TOOD a good use-case to use mocking
        @Suppress("KotlinConstantConditions")
        if (BuildConfig.RUN_CI)
            binding.hWeatherGalery.visibility = View.INVISIBLE
        else
            binding.hWeatherGalery.visibility = View.VISIBLE

        trackLoc = Location("trackLocation")
        trackLoc!!.latitude = SecHelper.entcryptXtude(trackRec.latitude)
        trackLoc!!.longitude = SecHelper.entcryptXtude(trackRec.longitude)
        binding.distanceContainer.trDetailDistance.tag = trackLoc
        binding.trDetailCoordinates.tag = trackLoc
        val prefs = MxPreferences.getInstance()
        if (prefs.firstTimeUse && !MxCoreApplication.isEmulator) {
            MxCoreApplication.showDlgHtml(
                requireActivity(),
                Html.fromHtml(requireActivity().getString(R.string.firstime_title)),
                Html.fromHtml(
                    String.format(
                        requireActivity().getString(R.string.firstime_dlg1),
                        "<b>&quot;" + requireActivity().getString(R.string.edit_track) + "&quot;</b>"
                    ) + "<br/><br/>" +
                            String.format(
                                requireActivity().getString(R.string.firstime_dlg2),
                                "<b>&quot;" + requireActivity().getString(R.string.feedback) + "&quot;</b>"
                            )
                )
            )
            prefs.edit().putFirstTimeUse(false).commit()
        }

        if (permissionHelper.hasLocationPermission()) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                DistanceHelper.checkDistance4View(recordTrack!!, binding.distanceContainer.trDetailDistance, location)
            }
        }
        DistanceHelper.checkDistance4View(recordTrack!!, binding.distanceContainer.trDetailDistance, null)
        addDisposable(
            mxDatabase.commentDao().avgByTrackId(trackRec.restId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ binding.trDetailRating.rating = it }, { _ -> Timber.e("Subscribing to registerRx failed") })
        )
        binding.trDetailDifficult.rating = trackRec.schwierigkeit.toFloat()

        addDisposable(
            mxDatabase.commentDao().avgByTrackId(trackRec.restId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { count ->
                    binding.textRatingCount.text = if (count == 0F) "" else String.format(getString(R.string.rating_count), count!!.toString() + "")
                }
        )
        binding.trLayoutDifficult.visibility = if (trackRec.schwierigkeit == 0L) View.GONE else View.VISIBLE
        binding.trLayoutRating.visibility = if (trackRec.trackaccess == "D") View.GONE else View.VISIBLE
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
        binding.trLayoutInfo.visibility = if (trackRec.mxtrack == 1L ||
            trackRec.quad == 1L ||
            trackRec.a4x4 == 1L ||
            trackRec.endruo == 1L ||
            trackRec.utv == 1L ||
            trackRec.facebook != null &&
            trackRec.facebook != ""
        )
            View.VISIBLE
        else
            View.GONE
        binding.imgMx.visibility = if (trackRec.mxtrack == 1L) View.VISIBLE else View.GONE
        binding.imgQuad.visibility = if (trackRec.quad == 1L) View.VISIBLE else View.GONE
        binding.img4x4.visibility = if (trackRec.a4x4 == 1L) View.VISIBLE else View.GONE
        binding.imgEnduro.visibility = if (trackRec.endruo == 1L) View.VISIBLE else View.GONE
        binding.imgUtv.visibility = if (trackRec.utv == 1L) View.VISIBLE else View.GONE
        binding.imgFacebook.visibility = if (trackRec.facebook != null && trackRec.facebook != "") View.VISIBLE else View.GONE
        binding.imgFacebook.setOnClickListener {
            this@FragmentTrackDetail.openFaceBook(this@FragmentTrackDetail.requireActivity(), trackRec.facebook)
        }
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
        binding.trLayoutCamping.visibility = if (trackRec.trackaccess == "D" || trackRec.camping == 0L) View.GONE else View.VISIBLE
        binding.trDetailShower.text = getString(if (trackRec.shower == 1L) R.string.yes else R.string.no)
        binding.trLayoutShower.visibility = if (trackRec.trackaccess == "D" || trackRec.shower == 0L) View.GONE else View.VISIBLE
        binding.trDetailElectricity.text = getString(if (trackRec.electricity == 1L) R.string.yes else R.string.no)
        binding.trLayoutElect.visibility = if (trackRec.trackaccess == "D" || trackRec.electricity == 0L) View.GONE else View.VISIBLE
        binding.trDetailBikecleaning.text = getString(if (trackRec.cleaning == 1L) R.string.yes else R.string.no)
        binding.trLayoutBikclean.visibility = if (trackRec.trackaccess == "D" || trackRec.cleaning == 0L) View.GONE else View.VISIBLE
        binding.trDetailKids.text = getString(if (trackRec.kidstrack == 1L) R.string.yes else R.string.no)
        binding.trLayoutKids.visibility = if (trackRec.trackaccess == "D" || trackRec.kidstrack == 0L) View.GONE else View.VISIBLE
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
            binding.trDetailLength.text = trackRec.tracklength.toString() + ""
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

        if (trackRec.trackaccess != "D") {
            binding.trLayoutAdress.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutAdress.visibility = LinearLayout.VISIBLE
            binding.trDetailAdress.text = SecHelper.decryptB64(trackRec.adress)
        }

        if (trackRec.trackaccess != "D") {
            binding.trLayoutBrands.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutBrands.visibility = LinearLayout.VISIBLE
            binding.trDetailBrands.text = trackRec.brands
        }

        if (trackRec.trackaccess != "D") {
            binding.trLayoutWorkshop.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutWorkshop.visibility = LinearLayout.VISIBLE
            binding.trDetailWorkshop.text = getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        }

        if (trackRec.trackaccess != "D") {
            binding.trLayoutShowroom.visibility = LinearLayout.GONE
        } else {
            binding.trLayoutShowroom.visibility = LinearLayout.VISIBLE
            binding.trDetailShowroom.text = getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        }

        // TODO
        // imEventLogo.setImageResource(TrackHelper
        // .getEventDrawableID(trackRec.getTrackpredicate(), trackRec.getCountry()));
        binding.trDetailEventlogo.visibility = View.GONE

        binding.trDetailEvent.text = trackRec.nuEvents
        binding.trLayoutEvent.visibility = LinearLayout.GONE

        val manager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val isoSim = manager.simCountryIso
        binding.trLayoutCoordinates.visibility =
            if (MxCoreApplication.isAdmin || isoSim.uppercase(Locale.ROOT) == "CH") LinearLayout.VISIBLE else LinearLayout.GONE
        binding.trDetailCoordinates.text = (SecHelper.entcryptXtude(trackRec.latitude).toString() + " " + SecHelper.entcryptXtude(trackRec.longitude))

        binding.trDetailLicense.text = trackRec.licence
        binding.trLayoutLicense.visibility = if (trackRec.trackaccess == "D" || trackRec.licence == null || trackRec.licence == "")
            View.GONE
        else
            View.VISIBLE
        binding.trDetailSx.text = getString(if (trackRec.supercross == 1L) R.string.yes else R.string.no)
        binding.trLayoutSx.visibility = if (trackRec.trackaccess == "D" || trackRec.supercross == 0L) View.GONE else View.VISIBLE

        binding.trLayoutLocation.visibility = View.VISIBLE
        when (trackRec.indoor.toInt()) {
            0 -> binding.trDetailLocation.text = getString(R.string.outdoor)
            1 -> binding.trDetailLocation.text = getString(R.string.indoor)
            else -> binding.trLayoutLocation.visibility = View.GONE
        }

        fillNextPrevId(trackRec.id)
    }

    private fun openFaceBook(activity: Activity, facebook: String) {
        try {
            activity.startActivity(getFaceBookIntent(SecHelper.decryptB64(facebook)))
        } catch (_: ActivityNotFoundException) {
            showInfo(activity, activity.getString(R.string.wrong_facebook_url))
        }

    }

    private fun getFaceBookIntent(facebook: String): Intent {
        //        try {
        //            context.getPackageManager().getPackageInfoCompat("com.facebook.katana", 0);
        //            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://groups/1435212740065304"));
        //        } catch (Exception e) {
        //            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/1435212740065304"));
        //        }
        return Intent(Intent.ACTION_VIEW, facebook.toUri())
    }

    private fun doOpenMap(loc: Location) {
        val qWfIntent = Intent(requireContext(), ActivityMapExtension::class.java)
        val bundle = Bundle()
        bundle.putDouble(ActivityMapExtension.LAT, loc.latitude)
        bundle.putDouble(ActivityMapExtension.LON, loc.longitude)
        qWfIntent.putExtras(bundle)
        startActivity(qWfIntent)
    }

    private fun toggleFavorite(item: MenuItem?) {
        val recTrack = TracksRecord.get(recordLocalId)
        if (recTrack != null) {
            val isFavorite = SQuery.newQuery().expr(Favorits.TRACK_REST_ID, Op.EQ, recTrack.restId).exists(Favorits.CONTENT_URI)
            if (isFavorite) {
                SQuery.newQuery().expr(Favorits.TRACK_REST_ID, Op.EQ, recTrack.restId).delete(Favorits.CONTENT_URI)
                showMessage(getString(R.string.favorite_delete), Snackbar.LENGTH_SHORT)
            } else {
                val rec = FavoritsRecord()
                rec.trackRestId = recTrack.restId
                rec.save()
                showMessage(getString(R.string.favorite_add), Snackbar.LENGTH_SHORT)
            }
            item?.setIcon(if (!isFavorite) R.drawable.gold_star2x else R.drawable.star2x)
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_track_detail, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                if (requireArguments().containsKey(IN_SLIDER) && requireArguments().getBoolean(IN_SLIDER)) {
                    menu.findItem(R.id.menu_detail_globus).isVisible = false
                    menu.findItem(R.id.menu_detail_radar).isVisible = false
                    menu.findItem(R.id.menu_event_add).isVisible = false

                    menu.findItem(R.id.menu_detail_radar)
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                    menu.findItem(R.id.menu_detail_globus).setShowAsActionFlags(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                    )
                    menu.findItem(R.id.menu_track_edit).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                    menu.findItem(R.id.menu_navigation).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                    menu.findItem(R.id.menu_favorite).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                } else {
                    menu.findItem(R.id.menu_detail_globus).isVisible = true
                }
                menu.findItem(R.id.menu_navigation).isVisible = permissionHelper.hasLocationPermission()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_track_edit -> {
                        openEdit(recordLocalId)
                        true
                    }

                    R.id.menu_event_add -> {
                        addEvent()
                        true
                    }

                    R.id.menu_navigation -> {
                        doOpenNavigation()
                        true
                    }

                    R.id.menu_detail_globus -> {
                        val recTrack = TracksRecord.get(recordLocalId)
                        if (recTrack != null) {
                            trackLoc = Location("trackloc")
                            trackLoc!!.latitude = SecHelper.entcryptXtude(recTrack.latitude)
                            trackLoc!!.longitude = SecHelper.entcryptXtude(recTrack.longitude)
                            doOpenMap(trackLoc!!)
                        }
                        true
                    }

                    R.id.menu_detail_radar -> {
                        false
                    }

                    R.id.menu_detail_share -> {
                        doShare()
                        false
                    }

                    R.id.menu_favorite -> {
                        toggleFavorite(menuItem)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun addEvent() {
        TracksRecord.get(recordLocalId)?.let {
            EventHelper().doAddEvent(requireActivity(), it)
        }
    }

    fun addRating() {
        TracksRecord.get(recordLocalId)?.let {
            showNewCommentDialog(it)
        }
    }

    private fun showNewCommentDialog(tracksRecord: TracksRecord) {
        val bindingDialog: LayoutCommentAddBinding = LayoutCommentAddBinding
            .inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(activity)
        builder.setView(bindingDialog.root)
        builder.setTitle(requireContext().getString(R.string.newRating))
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
        builder.setPositiveButton(R.string.done) { _, _ ->

            val comment = Comment()
            comment.approved = 0
            comment.id = System.currentTimeMillis() * -1000 //a negative value means it's not synced
            comment.trackId = tracksRecord.restId
            comment.rating = bindingDialog.comRating.rating.toInt()
            comment.username = bindingDialog.comUsername.text.toString()
            comment.note = bindingDialog.comNote.text.toString()
            comment.country = Locale.getDefault().country
            comment.changed = System.currentTimeMillis() / 1000
            comment.androidid = requireContext().getAndroidID()

            if (commentViewModel.addComment(comment, bindingDialog.comNote.text.toString()) > 0)
                Toast.makeText(activity, requireContext().getString(R.string.comment_alredy_exists), Toast.LENGTH_SHORT).show()

            if (comment.username != requireContext().getString(R.string.noname)) {
                val prefs1 = MxPreferences.getInstance()
                prefs1.edit().putUsername(comment.username.trim()).commit()
            }
        }

        val prefs = MxPreferences.getInstance()
        bindingDialog.comUsername.setHint(R.string.default_username)
        bindingDialog.comUsername.setText(prefs.username)
        val dlg = builder.create()
        dlg.show()
    }

    private fun openEdit(id: Long) {
        val qWfIntent = Intent(requireActivity(), ActivityTrackEdit::class.java)
        val bundle = Bundle()
        bundle.putLong(RECORD_ID_LOCAL, id)
        qWfIntent.putExtras(bundle)
        startActivity(qWfIntent)
    }

    private fun doShare() {
        val record = TracksRecord.get(recordLocalId)
        if (record != null) {
            val lat = SecHelper.entcryptXtude(record.latitude)
            val lon = SecHelper.entcryptXtude(record.longitude)
            val message = record.trackname + "\nhttps://www.google.de/maps/@" + lat + "," + lon + ",11z"
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, message)

            startActivity(Intent.createChooser(share, requireActivity().getText(R.string.share)))
        }
    }

    private fun doOpenNavigation() {
        val record = TracksRecord.get(recordLocalId)
        if (record != null) {
            LocationHelper.openNavi(
                requireContext(), record.trackname,
                SecHelper.entcryptXtude(record.latitude),
                SecHelper.entcryptXtude(record.longitude)
            )
        }
    }

    override fun onImportTaskComplete(results: String) {
        showMessage(
            if (results.contains("COMMIT")) getString(R.string.uploadsuccess) else getString(R.string.uploadfail),
            Snackbar.LENGTH_LONG
        )
    }

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        return when (loader) {
            LOADER_TRACK -> SQuery.newQuery().expr(Tracksges._ID, Op.EQ, bundle!!.getLong(RECORD_ID_LOCAL))
                .createSupportLoader(Tracksges.CONTENT_URI, null, null)

            LOADER_WEATHER -> SQuery.newQuery()
                .expr(Weather.TRACK_CLIENT_ID, Op.EQ, bundle!!.getLong(RECORD_ID_LOCAL))
                .expr(Weather.TYPE, Op.EQ, "D")
                .createSupportLoader(Weather.CONTENT_URI, null, Weather.DT)

            //LOADER_ROUTE
            else -> SQuery.newQuery()
                .expr(Route.TRACK_CLIENT_ID, Op.EQ, bundle!!.getLong(RECORD_ID_LOCAL))
                .createSupportLoader(Route.CONTENT_URI, null, Route.CREATED)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_TRACK -> {
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                if (cursor.count == 1) {
                    recordTrack = TracksgesRecord.fromCursor(cursor)
                    if (permissionHelper.hasLocationPermission()) {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            location?.let {
                                Ops.execute(AbstractOpGetRouteOperation.newIntent(recordTrack!!.id, location.latitude, location.longitude))
                                restartLastLocationDisplay(cursor, location)
                            }
                        }
                    }
                    restartLastLocationDisplay(cursor, null)
                }
            }

            LOADER_WEATHER -> {
                Timber.i("onLoadFinished Weather: ${cursor.count} TrackRestId: ${recordTrack!!.restId} ${recordTrack!!.trackname}")
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                try {
                    if (cursor.count > 0) {
                        val recordWeather = WeatherRecord.fromCursor(cursor)
                        adapterWeather!!.setTrackClientID(recordWeather.trackClientId)
                    }
                    adapterWeather!!.swapCursor(cursor)
                } catch (_: StaleDataException) {
                }

            }

            LOADER_ROUTE -> {
                Timber.i("onLoadFinished Route: %s TrackRestId:%s %s", cursor.count, recordTrack!!.restId, recordTrack!!.trackname)
                if (cursor.isBeforeFirst) {
                    cursor.moveToFirst()
                }
                if (cursor.count > 0) {
                    val routeRecord = RouteRecord.fromCursor(cursor)
                    DistanceHelper.setDistanceString(routeRecord, binding.distanceContainer.trDetailDistance)
                }
            }
        }
    }

    private fun restartLastLocationDisplay(cursor: Cursor, lastLocation: Location?) {
        recordTrackDist = TracksDistanceRecord.fromCursor(cursor, lastLocation)
        fillMask(recordTrack)
        val bundle = Bundle()
        bundle.putLong(TRACK_REST_ID, recordTrack!!.restId)
        bundle.putLong(RECORD_ID_LOCAL, recordTrack!!.id)
        loaderManager.restartLoader(LOADER_WEATHER, bundle, this)
        loaderManager.restartLoader(LOADER_ROUTE, bundle, this)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_ROUTE -> {
            }

            LOADER_WEATHER -> adapterWeather!!.swapCursor(null)
        }
    }

    private fun hasImageCaptureBug(): Boolean {
        // list of known devices that have the bug
        val devices = ArrayList<String>()
        devices.add("android-devphone1/dream_devphone/dream")
        devices.add("generic/sdk/generic")
        devices.add("vodafone/vfpioneer/sapphire")
        devices.add("tmobile/kila/dream")
        devices.add("verizon/voles/sholes")
        devices.add("google_ion/google_ion/sapphire")

        return devices.contains(Build.BRAND + "/" + Build.PRODUCT + "/" + Build.DEVICE)
    }

    @SuppressLint("SdCardPath")
    private fun doPictureMake() {
        // make photo
        fileAbsolute = (MxCoreApplication.getFilesDir(requireActivity()) + File.separatorChar + PREFIX_IMAGE_NAME +
                System.currentTimeMillis() +
                IMAGE_EXTENSION)
        val filePath = File(fileAbsolute!!)
        val imageUri = Uri.fromFile(filePath)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (hasImageCaptureBug()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File("/sdcard/tmp")))
        } else {
            // intent.putExtra(MediaStore.EXTRA_OUTPUT, Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        intent.putExtra("return-data", true)
        intent.putExtra("photo_type", "take") // Mark as take photo for result handling
        takePhotoLauncher.launch(intent)
    }

    fun doPicturePick() {
        if (permissionHelper.hasExternalStoragePermission()) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            pickPhotoLauncher.launch(photoPickerIntent)
        } else {
            requestStoragePermissionLauncher.launch(permission.READ_EXTERNAL_STORAGE)
        }
    }

    override fun onLocationChanged(location: Location) {
        if (recordTrackDist != null) {
            if (recordTrackDist!!.storeDistance2DB(location) > 0) {
                recordTrack!!.reload()
            }
            DistanceHelper.checkDistance4View(recordTrack!!, binding.distanceContainer.trDetailDistance, location)
        }
        if (recordTrack != null) {
            Ops.execute(AbstractOpGetRouteOperation.newIntent(recordTrack!!.id, location.latitude, location.longitude))
        }
    }

    private fun setUpLocationClientIfNeeded() {
        // Setup is no longer needed with FusedLocationProviderClient
        // Location updates are started directly in onResume
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (isAdded &&
            ActivityCompat.checkSelfPermission(requireActivity(), permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.requestLocationUpdates(LocationJobService.REQUEST_DAY, this, Looper.getMainLooper())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { it: Location ->
                    onLocationChanged(it)
                } ?: kotlin.run {
                    // Handle Null case or Request periodic location update https://developer.android.com/training/location/receive-location-updates
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        if (isAdded) {
            LocationServices.getFusedLocationProviderClient(requireContext()).removeLocationUpdates(this)
        }
    }

    override fun fillNextPrevId(localId: Long) {
        var isFav = false
        var sort: String? = ""
        currPosition = -1
        prevLocalId = 0L
        nextLocalId = 0L
        if (requireArguments().containsKey(CURSOR_POSITION)) {
            currPosition = requireArguments().getInt(CURSOR_POSITION)
            if (requireArguments().containsKey(ORDER)) {
                sort = requireArguments().getString(ORDER)
                if (sort == null) {
                    sort = TracksGesSum.TRACKNAME
                } else {
                    sort = sort.lowercase(Locale.getDefault())
                }
                isFav = sort.equals(IS_FAVORITE)
            }
        }
        var query = SQuery.newQuery()
        query = QueryHelper.buildUserTrackSearchFilter(
            query, requireArguments().getString(FILTER), isFav,
            AbstractMxInfoDBOpenHelper.Sources.TRACKSGES
        )
        val curs = query.select(Tracksges.CONTENT_URI, arrayOf(Tracksges._ID), sort)
        // from Map there is no position
        if (!requireArguments().containsKey(CURSOR_POSITION)) {
            while (curs.moveToNext()) {
                if (curs.getLong(0) == localId) {
                    currPosition = curs.position
                    requireArguments().putInt(CURSOR_POSITION, currPosition)
                    break
                }
            }
        }
        try {
            if (currPosition > 0) {
                curs.moveToPosition(currPosition - 1)
                prevLocalId = curs.getLong(0)
            }
            if (currPosition < curs.count - 1) {
                curs.moveToPosition(currPosition + 1)
                nextLocalId = curs.getLong(0)
            }
        } catch (_: Exception) {
        }

        curs.close()
    }

    private fun showMessage(text: String, lengthLong: Int) {
        if (requireActivity() is ActivityTrackDetail) {
            (requireActivity() as ActivityTrackDetail).showMessage(text, lengthLong)
        } else if (requireActivity() is ActivityMapExtension) {
            (requireActivity() as ActivityMapExtension).showMessage(text, lengthLong)
        }
    }

    /**
     * Insert an image into MediaStore using the modern ContentResolver API
     * to replace deprecated MediaStore.Images.Media.insertImage()
     */
    @Throws(IOException::class, FileNotFoundException::class)
    private fun insertImageToMediaStore(contentResolver: android.content.ContentResolver, imageFile: File): Uri? {
        if (!imageFile.exists()) {
            throw FileNotFoundException("Image file not found: ${imageFile.absolutePath}")
        }

        val imageFileName = imageFile.name
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
            }
        }

        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        return imageUri
    }

    companion object {
        const val DATA = "data"
        private var trackLoc: Location? = null

        private const val PREFIX_IMAGE_NAME = "img_"
        private const val IMAGE_EXTENSION = ".jpg"
        private const val CAPTURED_FILENAME = "captured_filename"
        const val TRACK_REST_ID = "track_rest_id"
        private const val LOADER_TRACK = 2
        private const val LOADER_WEATHER = 3
        private const val LOADER_ROUTE = 4
        const val IN_SLIDER = "inSlider"

        fun openWebSite(activity: Activity, urls: String) {
            if (urls.contains("\n")) {
                doSelectDlg(activity, R.string.website, urls)
            } else {
                var url = urls
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://$url"
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = url.toUri()
                if (intent.resolveActivity(activity.packageManager) != null) {
                    try {
                        activity.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        Timber.e("ActivityNotFoundException ACTION_VIEW not possible for%s", url)
                        if (url.startsWith("http://")) {
                            openWebSite(activity, urls.replace("http://", "https://"))
                        }
                    }

                } else
                    Timber.e("ACTION_VIEW not possible for %s", url)
            }
        }

        private fun doSelectDlg(activity: Activity, which: Int, gesString: String) {
            val alertChoice: Dialog
            val sortItems = gesString.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val builder = AlertDialog.Builder(activity)
            // telephone with an icon
            if (which == R.string.phone) {
                val inflater = activity.layoutInflater

                @SuppressLint("InflateParams")
                val viewHeader = inflater.inflate(R.layout.dialog_header_image, null)
                (viewHeader.findViewById<View>(R.id.dialogheader_button) as ImageView).setImageResource(android.R.drawable.ic_menu_call)
                (viewHeader.findViewById<View>(R.id.dialogheader_text) as TextView).text = activity.getString(which)
                builder.setCustomTitle(viewHeader)
            } else {
                builder.setTitle(activity.getString(which))
            }
            builder.setItems(sortItems) { _, item ->
                //FIXME
                //                alertChoice.dismiss();
                if (which == R.string.phone) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = ("tel:" + sortItems[item]).toUri()
                    activity.startActivity(intent)
                } else if (which == R.string.website) {
                    var url = sortItems[item]
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$url"
                    }
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = url.toUri()
                    activity.startActivity(i)
                }
            }
            alertChoice = builder.create()
            alertChoice.show()
        }
    }
}
