package info.mx.tracks.map

import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import com.sothree.slidinguppanel.PanelState
import info.hannes.changelog.ChangeLog
import info.hannes.commonlib.utils.setPhoneHasNoOptionsBtn
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.R
import info.mx.tracks.base.ActivityDrawerBase
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.settings.ActivitySetting
import info.mx.tracks.trackdetail.FragmentTrackDetailTab
import kotlin.math.roundToInt

class ActivityMapExtension : ActivityDrawerBase() {
    private var fabPadding: Int = 0
    private var displayHeight: Int = 0
    private var menuFab: FloatingActionMenu? = null
    private lateinit var toolbar: Toolbar
    private var scalar4Slide: Float = 0.toFloat()

    private val mapFragment: FragmentMap?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentMap) as FragmentMap?

    private val detailFragment: FragmentTrackDetailTab?
        get() = supportFragmentManager.findFragmentByTag(FragmentTrackDetailTab.TAG) as FragmentTrackDetailTab?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val isTablet = resources.getBoolean(R.bool.isTablet)
        scalar4Slide = if (isTablet) 0.85f else 0.78f
        this.setPhoneHasNoOptionsBtn()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Apply window insets to AppBarLayout to avoid overlap with status bar
        val appBarLayout = findViewById<View>(R.id.app_bar_layout)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                0,
                systemBars.top,
                0,
                0
            )
            insets
        }

        menuFab = findViewById(R.id.menuFab)
        menuFab?.setClosedOnTouchOutside(true)
        menuFab?.visibility = View.GONE
        menuFab?.setOnMenuToggleListener { opened ->
            if (opened) {
                menuFab?.alpha = 1f
            }
        }

        findViewById<View>(R.id.fabNewTrack).visibility = View.GONE
        findViewById<View>(R.id.fabNewTrack).setOnClickListener {
            menuFab?.close(false)
            mapFragment?.addNewTrack()
        }
        findViewById<View>(R.id.fabEvent).setOnClickListener {
            menuFab?.close(false)
            detailFragment?.addEvent()
        }
        findViewById<View>(R.id.fabPictures).setOnClickListener {
            menuFab?.close(false)
            detailFragment?.doPicturePick()
        }
        findViewById<View>(R.id.fabComment).setOnClickListener {
            menuFab?.close(false)
            detailFragment?.addRating()
        }

        fabPadding = resources.getDimension(R.dimen.fab_padding).roundToInt()
        displayHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val size = Point()
            @Suppress("DEPRECATION")
            display.getSize(size)
            size.y
        }

        MxPreferences.getInstance().edit().putLastOpenStartActivity(this@ActivityMapExtension.javaClass.simpleName).apply()

        // Setup back press handling using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mapFragment?.onFragmentBackPressed() == true) {
                    // If the fragment indicates it's done processing back press, finish the activity
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
                // If the fragment handled the back press (returned false), do nothing
            }
        })

        val cl = ChangeLog(this)
        if (cl.isFirstRunEver) {
            val settingIntent = Intent(this, ActivitySetting::class.java)
            startActivity(settingIntent)
        } else if (cl.isFirstRun && !MxCoreApplication.isEmulator) {
            cl.fullLogDialog.show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mapFragment?.onFragmentBackPressed()!!) {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun setFabPosition(panelState: PanelState, headerHeight: Int, position: Float) {
        when (panelState) {
            PanelState.EXPANDED -> Unit

            PanelState.COLLAPSED -> {
                menuFab?.visibility = View.VISIBLE
                menuFab?.setPadding(fabPadding, fabPadding, fabPadding, headerHeight + fabPadding)
            }

            PanelState.ANCHORED -> menuFab?.visibility = View.VISIBLE

            PanelState.HIDDEN -> {
                menuFab?.visibility = View.GONE
                menuFab?.setPadding(fabPadding, fabPadding, fabPadding, fabPadding)
            }

            PanelState.DRAGGING -> {
                menuFab?.close(false)
                menuFab?.alpha = 1 - position
                menuFab?.setPadding(
                    fabPadding, fabPadding, fabPadding,
                    ((displayHeight - toolbar.height - headerHeight).toFloat() * position * scalar4Slide).roundToInt() + headerHeight + fabPadding
                )
            }
        }
    }

    fun showMessage(text: String, length: Int) {
        val fabMenu = findViewById<FloatingActionMenu>(R.id.menuFab)
        Snackbar.make(fabMenu, text, length).setAction("Action", null).show()
    }

    companion object {

        const val LON = "lon"
        const val LAT = "lat"
    }
}
