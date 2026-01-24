package info.mx.tracks.util

import android.app.Activity
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * An API 11+ implementation of [SystemUiHider]. Uses modern WindowInsetsController APIs
 * to show and hide the system UI.
 *
 * Constructor not intended to be called by clients. Use
 * [SystemUiHider.getInstance] to obtain an instance.
 */
class SystemUiHiderHoneycomb(activity: Activity, anchorView: View, flags: Int) : SystemUiHiderBase(activity,
    flags) {

    private val windowInsetsController: WindowInsetsControllerCompat =
        WindowCompat.getInsetsController(activity.window, anchorView)

    /**
     * Whether or not the system UI is currently visible. This is cached from
     * window insets callbacks.
     */
    private var mVisible = true

    /**
     * Types of insets to hide based on flags
     */
    private var insetsToHide = 0

    init {
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        // Determine which insets to hide based on flags
        insetsToHide = if (flags and FLAG_FULLSCREEN != 0) {
            WindowInsetsCompat.Type.statusBars()
        } else {
            0
        }

        if (flags and FLAG_HIDE_NAVIGATION != 0) {
            insetsToHide = insetsToHide or WindowInsetsCompat.Type.navigationBars()
        }

        if (insetsToHide == 0) {
            insetsToHide = WindowInsetsCompat.Type.systemBars()
        }

        // Set up insets listener to track visibility
        anchorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = WindowInsetsCompat.toWindowInsetsCompat(windowInsets, view)
            // Use isVisible() which is compatible with API 21+
            val visible = insets.isVisible(WindowInsetsCompat.Type.statusBars()) ||
                         insets.isVisible(WindowInsetsCompat.Type.navigationBars())

            if (visible != mVisible) {
                mVisible = visible
                mOnVisibilityChangeListener.onVisibilityChange(visible)
            }

            windowInsets
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun setup() {
        // Setup is handled in init block with setOnApplyWindowInsetsListener
    }

    /**
     * {@inheritDoc}
     */
    override fun hide() {
        windowInsetsController.hide(insetsToHide)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        mVisible = false
    }

    /**
     * {@inheritDoc}
     */
    override fun show() {
        windowInsetsController.show(insetsToHide)
        mVisible = true
    }

    /**
     * {@inheritDoc}
     */
    override fun isVisible(): Boolean {
        return mVisible
    }
}
