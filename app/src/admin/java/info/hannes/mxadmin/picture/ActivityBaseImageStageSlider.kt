package info.hannes.mxadmin.picture

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mechadminGen.sqlite.MxAdminDBContract
import info.hannes.mechadminGen.sqlite.PictureStageRecord
import info.mx.tracks.R
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ImageCursorAdapter
import info.mx.tracks.trackdetail.ImageCursorAdapter.OnImageListItemClick
import info.mx.tracks.util.SystemUiHider
import info.mx.tracks.util.SystemUiHider.OnVisibilityChangeListener
import info.mx.tracks.views.NoCrashViewPager
import timber.log.Timber

abstract class ActivityBaseImageStageSlider : ActivityRx(), LoaderManager.LoaderCallbacks<Cursor?>,
    OnImageListItemClick, OnPageChangeListener {
    private var imageViewPager: NoCrashViewPager? = null
    private var imageSwipePagerAdapter: AdapterImageStagePager? = null

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        Timber.d("%s", position)
        thumbsGallery!!.scrollToPosition(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    /**
     * The instance of the [SystemUiHider] for this activity.
     */
    private var mSystemUiHider: SystemUiHider? = null
    private var trackId: Long = 0
    private var imageClientId: Long = 0
    protected var trackRestId: Long = 0
    private var thumbsAdapter: ImageCursorAdapter? = null

    private var thumbsGallery: RecyclerView? = null
    protected var currPictureRestId: Long = 0
    protected var thumbsCursor: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.content_activity_image_slider)

        trackId = this.intent.getLongExtra(TRACK_ID, 0)
        imageClientId = this.intent.getLongExtra(IMAGE_CLIENT_ID, 0)

        val record = TracksRecord.get(trackId)
        if (record != null) {
            trackRestId = record.restId
        }

        val controlsView = findViewById<View>(R.id.fullscreen_content_controls)
        thumbsGallery = findViewById<RecyclerView>(R.id.gallery_thumbs)

        thumbsAdapter = ImageCursorAdapter(
            this,
            null,
            Math.round(getResources().getDimension(R.dimen.thumbnail_size_dp)),
            false
        )
        val layoutRecycler = LinearLayoutManager(this)
        layoutRecycler.setOrientation(LinearLayoutManager.HORIZONTAL)
        thumbsGallery!!.setLayoutManager(layoutRecycler)
        thumbsGallery!!.setAdapter(thumbsAdapter)

        initViewPager()

        // Set up an instance of SystemUiHider to control the system UI for this activity.
        mSystemUiHider = SystemUiHider.getInstance(
            this,
            imageViewPager!!,
            SystemUiHider.FLAG_HIDE_NAVIGATION
        )
        //        mSystemUiHider = SystemUiHider.getInstance(this, imageView, HIDER_FLAGS);
        mSystemUiHider!!.setup()
        mSystemUiHider!!.setOnVisibilityChangeListener(object : OnVisibilityChangeListener {
            // Cached values.
            var mControlsHeight: Int = 0
            var mShortAnimTime: Int = 0

            override fun onVisibilityChange(visible: Boolean) {
                // If the ViewPropertyAnimator API is available (Honeycomb MR2 and later), use it to animate the
                // in-layout UI controls at the bottom of the screen.
                if (mControlsHeight == 0) {
                    mControlsHeight = controlsView.height
                }
                if (mShortAnimTime == 0) {
                    mShortAnimTime =
                        getResources().getInteger(android.R.integer.config_shortAnimTime)
                }
                controlsView.animate().translationY((if (visible) 0 else mControlsHeight).toFloat()).duration = mShortAnimTime.toLong()

                if (visible /* && AUTO_HIDE */) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS)
                }
            }
        })

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById<View>(R.id.gallery_thumbs).setOnTouchListener(mDelayHideTouchListener)
    }

    private fun initViewPager() {
        imageSwipePagerAdapter = AdapterImageStagePager(supportFragmentManager)
        imageViewPager = findViewById<NoCrashViewPager>(R.id.fragmentImagePager)
        imageViewPager!!.setAdapter(imageSwipePagerAdapter)
        imageViewPager!!.addOnPageChangeListener(this)
    }


    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackId = savedInstanceState.getLong(TRACK_ID)
        imageClientId = savedInstanceState.getLong(IMAGE_CLIENT_ID)

        val record = TracksRecord.get(trackId)
        if (record != null) {
            trackRestId = record.restId
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(TRACK_ID, trackId)
        outState.putLong(IMAGE_CLIENT_ID, imageClientId)

        super.onSaveInstanceState(outState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been created, to briefly hint to the user that UI controls are available.
        delayedHide(2000)
    }

    public override fun onResume() {
        super.onResume()
        supportLoaderManager.initLoader<Cursor?>(LOADER_PICTURE_THUMBS, null, this)
    }

    public override fun onPause() {
        supportLoaderManager.destroyLoader(LOADER_PICTURE_THUMBS)
        super.onPause()
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    var mDelayHideTouchListener: View.OnTouchListener =
        View.OnTouchListener { _: View?, _: MotionEvent? ->
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            false
        }

    var mHideHandler: Handler = Handler(Looper.getMainLooper())
    var mHideRunnable: Runnable = object : Runnable {
        override fun run() {
            mSystemUiHider!!.hide()
        }
    }

    /**
     * Schedules a call to hide() in [info.mx.tracks.util.Wait.delay] milliseconds, canceling any previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    protected fun setImage(record: PictureStageRecord) {
        if (thumbsCursor != null) {
            thumbsCursor!!.moveToFirst()
            thumbsCursor!!.moveToPrevious()
            var pos = -1
            while (thumbsCursor!!.moveToNext()) {
                val pic = PictureStageRecord.fromCursor(thumbsCursor)
                pos++
                if (pic.id == record.id) {
                    thumbsGallery!!.scrollToPosition(pos)
                    currPictureRestId = record.restId
                    Timber.i("set Img %s", pos)

                    break
                }
            }
        }
    }

    protected abstract val picturesQuery: SQuery

    override fun onCreateLoader(loader: Int, bundle: Bundle?): Loader<Cursor?> {
        val query = this.picturesQuery
        return query.createSupportLoader(
            MxAdminDBContract.PictureStage.CONTENT_URI,
            null,
            MxAdminDBContract.PictureStage.REST_ID
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> {
                thumbsCursor = cursor
                Timber.d("LOADER_PICTURE_THUMBS ${cursor?.count}")
                var position = -1
                cursor?.let { nonNullCursor ->
                    while (nonNullCursor.moveToNext()) {
                        position = nonNullCursor.position
                        val rec = PictureStageRecord.fromCursor(cursor)
                        if (rec.restId == imageClientId) {
                            setImage(rec)
                            break
                        }
                    }
                    thumbsAdapter!!.swapCursor(nonNullCursor)
                    if (imageSwipePagerAdapter!!.setCursor(nonNullCursor)) { //prevent flickering
                        imageViewPager!!.setCurrentItem(position)
                    }
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor?>) {
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> thumbsAdapter!!.swapCursor(null)
        }
    }

    override fun onImageItemClick(position: Int, imageRestId: Long) {
        this.imageClientId = imageRestId
        openImageFragment(position)
    }

    private fun openImageFragment(position: Int) {
        if (imageViewPager!!.adapter != null) {
            (imageViewPager!!.adapter as AdapterImageStagePager).resetZoom()
        }
        imageViewPager!!.setCurrentItem(position, true)
    }

    protected fun restartThumbsLoader() {
        supportLoaderManager.restartLoader<Cursor?>(LOADER_PICTURE_THUMBS, null, this)
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 4000

        /**
         * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon interaction.
         */
        protected const val LOADER_PICTURE_THUMBS: Int = 0
        const val TRACK_ID: String = "trackID"
        const val IMAGE_CLIENT_ID: String = "imageClientID"
    }
}
