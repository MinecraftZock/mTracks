package info.mx.tracks.image

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.robotoworks.mechanoid.db.SQuery
import info.mx.tracks.R
import info.mx.tracks.StorageCleanupManager
import info.mx.tracks.databinding.ActivityImageSlideBinding
import info.mx.tracks.sqlite.MxInfoDBContract.Pictures
import info.mx.tracks.sqlite.PicturesRecord
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ImageCursorAdapter
import info.mx.tracks.trackdetail.ImageCursorAdapter.OnImageListItemClick
import info.mx.tracks.util.SystemUiHider
import info.mx.tracks.util.SystemUiHider.Companion.getInstance
import info.mx.tracks.util.SystemUiHider.OnVisibilityChangeListener
import timber.log.Timber
import kotlin.math.roundToInt

abstract class ActivityBaseImageSlider : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, OnImageListItemClick {

    private lateinit var imageSwipePagerAdapter: AdapterImagePager

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            // Optional: handle scroll state changes
        }

        override fun onPageSelected(position: Int) {
            Timber.d("%s", position)
            binding.containerBaseImage.galleryThumbs.scrollToPosition(position)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            // Optional: handle page scroll
        }
    }

    private var systemUiHider: SystemUiHider? = null
    private var trackId: Long = 0
    private var imageRestId: Long = 0
    protected var trackRestId: Long = 0
    private lateinit var thumbsAdapter: ImageCursorAdapter
    private var currPictureRestId: Long = 0
    protected var thumbsCursor: Cursor? = null

    private lateinit var binding: ActivityImageSlideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSlideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        trackId = this.intent.getLongExtra(TRACK_ID, 0)
        imageRestId = this.intent.getLongExtra(IMAGE_ID, 0)
        val record = TracksRecord.get(trackId)
        if (record != null) {
            trackRestId = record.restId
        }
        thumbsAdapter = ImageCursorAdapter(
            this,
            null,
            resources.getDimension(R.dimen.thumbnail_size_dp).roundToInt(),
            false
        )
        val layoutRecycler = LinearLayoutManager(this)
        layoutRecycler.orientation = LinearLayoutManager.HORIZONTAL
        binding.containerBaseImage.galleryThumbs.layoutManager = layoutRecycler
        binding.containerBaseImage.galleryThumbs.adapter = thumbsAdapter

        imageSwipePagerAdapter = AdapterImagePager(this)
        binding.containerBaseImage.fragmentImagePager.adapter = imageSwipePagerAdapter
        binding.containerBaseImage.fragmentImagePager.registerOnPageChangeCallback(onPageChangeCallback)

        // Set up an instance of SystemUiHider to control the system UI for this activity.
        systemUiHider = getInstance(this, binding.containerBaseImage.fragmentImagePager, SystemUiHider.FLAG_HIDE_NAVIGATION)
        systemUiHider!!.setup()
        systemUiHider!!.setOnVisibilityChangeListener(object : OnVisibilityChangeListener {
            // Cached values.
            var controlsHeight = 0
            var shortAnimTime = 0
            override fun onVisibilityChange(visible: Boolean) {
                // If the ViewPropertyAnimator API is available (Honeycomb MR2 and later), use it to animate the
                // in-layout UI controls at the bottom of the screen.
                if (controlsHeight == 0) {
                    controlsHeight = binding.containerBaseImage.fullscreenContentControls.height
                }
                if (shortAnimTime == 0) {
                    shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
                }
                binding.containerBaseImage.fullscreenContentControls.animate()
                    .translationY(
                        if (visible)
                            0F
                        else
                            controlsHeight.toFloat()
                    )
                    .duration =
                    shortAnimTime.toLong()
                if (visible /* && AUTO_HIDE */) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS)
                }
            }
        })

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away while interacting with the UI.
        binding.containerBaseImage.galleryThumbs.setOnTouchListener(delayHideTouchListener)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackId = savedInstanceState.getLong(TRACK_ID)
        imageRestId = savedInstanceState.getLong(IMAGE_ID)
        TracksRecord.get(trackId)?.let {
            trackRestId = it.restId
        }
    }

    @SuppressLint("MissingSuperCall")
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(TRACK_ID, trackId)
        outState.putLong(IMAGE_ID, imageRestId)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been created, to briefly hint to the user that UI controls are available.
        delayedHide(2000)
    }

    public override fun onResume() {
        super.onResume()

        // Proactively check storage and cleanup old cached images if needed
        StorageCleanupManager.checkAndCleanupIfNeeded(this)

        supportLoaderManager.initLoader(LOADER_PICTURE_THUMBS, null, this)
    }

    public override fun onPause() {
        supportLoaderManager.destroyLoader(LOADER_PICTURE_THUMBS)
        super.onPause()
    }

    override fun onDestroy() {
        binding.containerBaseImage.fragmentImagePager.unregisterOnPageChangeCallback(onPageChangeCallback)
        imageSwipePagerAdapter.clearCache()
        super.onDestroy()
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    var delayHideTouchListener = View.OnTouchListener { _: View?, _: MotionEvent? ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }
    var hideHandler = Handler(Looper.getMainLooper())
    var hideRunnable = Runnable { systemUiHider!!.hide() }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    protected fun setImage(record: PicturesRecord) {
        thumbsCursor?.let {
            it.moveToFirst()
            it.moveToPrevious()
            var pos = -1
            while (it.moveToNext()) {
                val pic = PicturesRecord.fromCursor(it)
                pos++
                if (pic.id == record.id) {
                    binding.containerBaseImage.galleryThumbs.scrollToPosition(pos)
                    currPictureRestId = record.restId
                    Timber.i("set Img %s", pos)
                    break
                }
            }
        }
    }

    protected abstract val picturesQuery: SQuery
    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor> {
        val query = picturesQuery
        return query.createSupportLoader(Pictures.CONTENT_URI, null, Pictures.REST_ID)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> {
                thumbsCursor = cursor
                Timber.d("LOADER_PICTURE_THUMBS %s", cursor.count)
                var position = -1
                while (cursor.moveToNext()) {
                    position = cursor.position
                    val rec = PicturesRecord.fromCursor(cursor)
                    if (rec.restId == imageRestId) {
                        setImage(rec)
                        break
                    }
                }
                thumbsAdapter.swapCursor(cursor)
                if (imageSwipePagerAdapter.setCursor(cursor)) { //prevent flickering
                    binding.containerBaseImage.fragmentImagePager.currentItem = position
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> thumbsAdapter.swapCursor(null)
        }
    }

    private fun openImageFragment(position: Int) {
        if (binding.containerBaseImage.fragmentImagePager.adapter != null) {
            (binding.containerBaseImage.fragmentImagePager.adapter as AdapterImagePager?)!!.resetZoom()
        }
        binding.containerBaseImage.fragmentImagePager.setCurrentItem(position, true)
    }

    protected fun restartThumbsLoader() {
        supportLoaderManager.restartLoader(LOADER_PICTURE_THUMBS, null, this)
    }

    override fun onImageItemClick(position: Int, imageRestId: Long) {
        systemUiHider?.show()
        this.imageRestId = imageRestId
        openImageFragment(position)
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 4000

        /**
         * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon interaction.
         */
        protected const val LOADER_PICTURE_THUMBS = 0
        const val TRACK_ID = "trackID"
        const val IMAGE_ID = "imageID"
    }
}
