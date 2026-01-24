package info.mx.tracks.util

import android.app.Activity
import android.view.View

/**
 * A utility class that helps with showing and hiding system UI such as the
 * status bar and info.mx.tracks.navigation/system bar. This class uses backward-compatibility
 * techniques described in [
 * Creating Backward-Compatible UIs](http://developer.android.com/training/backward-compatible-ui/index.html) to ensure that devices running any
 * version of ndroid OS are supported. More specifically, there are separate
 * implementations of this abstract class: for newer devices,
 * [.getInstance] will return a [SystemUiHiderHoneycomb] instance,
 * while on older devices [.getInstance] will return a
 * [SystemUiHiderBase] instance.
 *
 *
 * For more on system bars, see [ System Bars](http://developer.android.com/design/get-started/ui-overview.html#system-bars).
 *
 * @see View.setSystemUiVisibility
 * @see android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
 *
 * The current UI hider flags.
 *
 * @see .FLAG_FULLSCREEN
 *
 * @see .FLAG_HIDE_NAVIGATION
 *
 * @see .FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
 */
abstract class SystemUiHider protected constructor(protected var activity: Activity, protected var flags: Int) {

    /**
     * The current visibility callback.
     */
    protected var mOnVisibilityChangeListener = sDummyListener

    /**
     * Returns whether or not the system UI is visible.
     */
    abstract fun isVisible(): Boolean

    /**
     * Sets up the system UI hider. Should be called from
     * [Activity.onCreate].
     */
    abstract fun setup()

    /**
     * Hide the system UI.
     */
    abstract fun hide()

    /**
     * Show the system UI.
     */
    abstract fun show()

    /**
     * Registers a callback, to be triggered when the system UI visibility
     * changes.
     */
    fun setOnVisibilityChangeListener(listener: OnVisibilityChangeListener?) {
        var listenerLocal = listener
        if (listenerLocal == null) {
            listenerLocal = sDummyListener
        }

        mOnVisibilityChangeListener = listenerLocal
    }

    /**
     * A callback interface used to listen for system UI visibility changes.
     */
    interface OnVisibilityChangeListener {
        /**
         * Called when the system UI visibility has changed.
         *
         * @param visible True if the system UI is visible.
         */
        fun onVisibilityChange(visible: Boolean)
    }

    companion object {
        /**
         * When this flag is set, the
         * [android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN]
         * flag will be set on older devices, making the status bar "float" on top
         * of the activity layout. This is most useful when there are no controls at
         * the top of the activity layout.
         *
         *
         * This flag isn't used on newer devices because the [action
         * bar](http://developer.android.com/design/patterns/actionbar.html), the most important structural element of an Android app, should
         * be visible and not obscured by the system UI.
         */
        const val FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1

        /**
         * When this flag is set, [.show] and [.hide] will toggle
         * the visibility of the status bar. If there is a info.mx.tracks.navigation bar, show and
         * hide will toggle low profile mode.
         */
        const val FLAG_FULLSCREEN = 0x2

        /**
         * When this flag is set, [.show] and [.hide] will toggle
         * the visibility of the info.mx.tracks.navigation bar, if it's present on the device and
         * the device allows hiding it. In cases where the info.mx.tracks.navigation bar is present
         * but cannot be hidden, show and hide will toggle low profile mode.
         */
        const val FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN or 0x4

        /**
         * Creates and returns an instance of [SystemUiHider] that is
         * appropriate for this device. The object will be either a
         * [SystemUiHiderBase] or [SystemUiHiderHoneycomb] depending on
         * the device.
         *
         * @param activity   The activity whose window's system UI should be controlled by
         * this class.
         * @param anchorView The view on which [View.setSystemUiVisibility] will
         * be called.
         * @param flags      Either 0 or any combination of [.FLAG_FULLSCREEN],
         * [.FLAG_HIDE_NAVIGATION], and
         * [.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES].
         */
        fun getInstance(activity: Activity, anchorView: View, flags: Int): SystemUiHider {
            return SystemUiHiderHoneycomb(activity, anchorView, flags)
        }

        /**
         * A dummy no-op callback for use when there is no other listener set.
         */
        private val sDummyListener: OnVisibilityChangeListener = object : OnVisibilityChangeListener {
            override fun onVisibilityChange(visible: Boolean) {}
        }
    }
}
