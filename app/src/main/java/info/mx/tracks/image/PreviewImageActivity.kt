package info.mx.tracks.image

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import info.mx.tracks.R

class PreviewImageActivity : Activity(), OnPageChangeListener {
    private var mRequestWaitingForBinder = false
    private var mFullScreenAnchorView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_image_activity)
        mFullScreenAnchorView = window.decorView

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // to keep our UI controls visibility in line with system bars visibility
        mFullScreenAnchorView!!.setOnApplyWindowInsetsListener { _, windowInsets ->
            val insets = WindowInsetsCompat.toWindowInsetsCompat(windowInsets)
            // Use isVisible() which is compatible with API 21+
            val visible = insets.isVisible(WindowInsetsCompat.Type.statusBars()) ||
                         insets.isVisible(WindowInsetsCompat.Type.navigationBars())
            if (visible) {
                actionBar?.show()
            } else {
                actionBar?.hide()
            }
            windowInsets
        }
        mRequestWaitingForBinder = savedInstanceState?.getBoolean(KEY_WAITING_FOR_BINDER) ?: false
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available
        delayedHide(INITIAL_HIDE_DELAY)
    }

    private var mHideSystemUiHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            hideSystemUI(mFullScreenAnchorView)
            actionBar!!.hide()
        }
    }

    @Suppress("SameParameterValue")
    private fun delayedHide(delayMillis: Int) {
        mHideSystemUiHandler.removeMessages(0)
        mHideSystemUiHandler.sendEmptyMessageDelayed(0, delayMillis.toLong())
    }

    /// handle Window Focus changes
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action.
        if (!hasFocus) {
            mHideSystemUiHandler.removeMessages(0)
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_WAITING_FOR_BINDER, mRequestWaitingForBinder)
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not necessarily
     * complete.
     *
     * @param position Position index of the new selected page
     */
    override fun onPageSelected(position: Int) {
        //TODO
        //        mSavedPosition = position;
        //        mHasSavedPosition = true;
        //        if (mDownloaderBinder == null) {
        //            mRequestWaitingForBinder = true;
        //
        //        } else {
        //            OCFile currentFile = mPreviewImagePagerAdapter.getFileAt(position);
        //            getSupportActionBar().setTitle(currentFile.getFileName());
        //            mDrawerToggle.setDrawerIndicatorEnabled(false);
        //            if (!currentFile.isDown()) {
        //                if (!mPreviewImagePagerAdapter.pendingErrorAt(position)) {
        //                    requestForDownload(currentFile);
        //                }
        //            }
        //
        //            // Call to reset image zoom to initial state
        //            ((PreviewImagePagerAdapter) mViewPager.getAdapter()).resetZoom();
        //        }
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user begins dragging,
     * when the pager is automatically settling to the current page. when it is fully stopped/idle.
     *
     * @param state The new scroll state (SCROLL_STATE_IDLE, _DRAGGING, _SETTLING
     */
    override fun onPageScrollStateChanged(state: Int) {}

    /**
     * This method will be invoked when the current page is scrolled, either as part of a
     * programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     * Page position+1 will be visible if positionOffset is
     * nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page
     * at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    @SuppressLint("InlinedApi")
    private fun hideSystemUI(anchorView: View?) {
        val windowInsetsController = WindowCompat.getInsetsController(window, anchorView!!)
        windowInsetsController.apply {
            // Hide both the status bar and navigation bar
            hide(WindowInsetsCompat.Type.systemBars())
            // Configure behavior: swipe to temporarily show system bars
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        private const val KEY_WAITING_FOR_BINDER = "WAITING_FOR_BINDER"
        private const val INITIAL_HIDE_DELAY = 0 // immediate hide
    }
}
