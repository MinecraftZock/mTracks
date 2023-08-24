package info.mx.tracks.util

import android.app.Activity
import android.view.View

/**
 * An API 11+ implementation of [SystemUiHider]. Uses APIs available in
 * Honeycomb and later (specifically [View.setSystemUiVisibility]) to
 * show and hide the system UI.
 *
 * Constructor not intended to be called by clients. Use
 * [SystemUiHider.getInstance] to obtain an instance.
 */
class SystemUiHiderHoneycomb(activity: Activity, anchorView: View, flags: Int) : SystemUiHiderBase(activity, anchorView, flags) {

    /**
     * Flags for [View.setSystemUiVisibility] to use when showing the
     * system UI.
     */
    private var mShowFlags: Int = 0

    /**
     * Flags for [View.setSystemUiVisibility] to use when hiding the
     * system UI.
     */
    private var mHideFlags: Int = 0

    /**
     * Flags to test against the first parameter in
     * [View.OnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange]
     * to determine the system UI visibility state.
     */
    private var mTestFlags: Int = 0

    /**
     * Whether or not the system UI is currently visible. This is cached from
     * [View.OnSystemUiVisibilityChangeListener].
     */
    private var mVisible = true

    private val mSystemUiVisibilityChangeListener = View.OnSystemUiVisibilityChangeListener { vis ->
        // Test against mTestFlags to see if the system UI is visible.
        if (vis and mTestFlags != 0) {

            // Trigger the registered listener and cache the visibility
            // state.
            mOnVisibilityChangeListener.onVisibilityChange(false)
            mVisible = false

        } else {
            this.anchorView.systemUiVisibility = mShowFlags

            // Trigger the registered listener and cache the visibility
            // state.
            mOnVisibilityChangeListener.onVisibilityChange(true)
            mVisible = true
        }
    }

    init {

        mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE
        mHideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
        mTestFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE

        if (this.flags and SystemUiHider.FLAG_FULLSCREEN != 0) {
            // If the client requested fullscreen, add flags relevant to hiding
            // the status bar. Note that some of these constants are new as of
            // API 16 (Jelly Bean). It is safe to use them, as they are inlined
            // at compile-time and do nothing on pre-Jelly Bean devices.
            mShowFlags = mShowFlags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            mHideFlags = mHideFlags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

        if (this.flags and SystemUiHider.FLAG_HIDE_NAVIGATION != 0) {
            // If the client requested hiding info.mx.tracks.navigation, add relevant flags.
            mShowFlags = mShowFlags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            mHideFlags = mHideFlags or (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            mTestFlags = mTestFlags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun setup() {
        anchorView.setOnSystemUiVisibilityChangeListener(mSystemUiVisibilityChangeListener)
    }

    /**
     * {@inheritDoc}
     */
    override fun hide() {
        anchorView.systemUiVisibility = mHideFlags
    }

    /**
     * {@inheritDoc}
     */
    override fun show() {
        anchorView.systemUiVisibility = mShowFlags
    }

    /**
     * {@inheritDoc}
     */
    override fun isVisible(): Boolean {
        return mVisible
    }
}
