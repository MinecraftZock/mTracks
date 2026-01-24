package info.mx.tracks.util

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * A base implementation of [SystemUiHider]. Uses APIs available in all API levels to show and hide the status bar.
 *
 * Constructor not intended to be called by clients. Use
 * [SystemUiHider.getInstance] to obtain an instance.
 */
open class SystemUiHiderBase(activity: Activity, flags: Int) : SystemUiHider(activity, flags) {
    /**
     * Whether or not the system UI is currently visible. This is a cached value
     * from calls to [.hide] and [.show].
     */
    private var mVisible = true

    override fun setup() {
        if (flags and FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES == 0) {
            activity.window
                .setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
        }
    }

    override fun isVisible(): Boolean {
        return mVisible
    }

    override fun hide() {
        if (flags and FLAG_FULLSCREEN != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Modern API (Android 11+): Use WindowInsetsController
                val decorView = activity.window.decorView
                val insetsController = WindowCompat.getInsetsController(activity.window, decorView)
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                // Legacy API: Use deprecated FLAG_FULLSCREEN for older devices
                @Suppress("DEPRECATION")
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }
        mOnVisibilityChangeListener.onVisibilityChange(false)
        mVisible = false
    }

    override fun show() {
        if (flags and FLAG_FULLSCREEN != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Modern API (Android 11+): Use WindowInsetsController
                val decorView = activity.window.decorView
                val insetsController = WindowCompat.getInsetsController(activity.window, decorView)
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            } else {
                // Legacy API: Clear FLAG_FULLSCREEN for older devices
                @Suppress("DEPRECATION")
                activity.window.setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
        mOnVisibilityChangeListener.onVisibilityChange(true)
        mVisible = true
    }
}
