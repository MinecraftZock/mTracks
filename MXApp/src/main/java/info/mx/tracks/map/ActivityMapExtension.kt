package info.mx.tracks.map

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import com.sothree.slidinguppanel.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import info.hannes.changelog.ChangeLog
import info.hannes.commonlib.utils.DeviceTools
import info.mx.tracks.R
import info.mx.tracks.base.ActivityDrawerBase
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.settings.ActivitySetting
import info.mx.tracks.trackdetail.FragmentTrackDetailTab
import kotlin.math.roundToInt

class ActivityMapExtension : ActivityDrawerBase() {
    private var fabPadding: Int = 0
    private var displayHeight: Int = 0
    private var fabMenu: FloatingActionMenu? = null
    private lateinit var toolbar: Toolbar
    private var scalar4Slide: Float = 0.toFloat()

    private val mapFragment: FragmentMap?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentMap) as FragmentMap?

    private val detailFragment: FragmentTrackDetailTab?
        get() = supportFragmentManager.findFragmentByTag(FragmentTrackDetailTab.TAG) as FragmentTrackDetailTab?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val isTablet = resources.getBoolean(R.bool.isTablet)
        scalar4Slide = if (isTablet) 0.85f else 0.78f
        DeviceTools.setPhoneHasNoOptionsBtn(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fabMenu = findViewById(R.id.menuFab)
        fabMenu?.setClosedOnTouchOutside(true)
        fabMenu?.visibility = View.GONE
        fabMenu?.setOnMenuToggleListener { opened ->
            if (opened) {
                fabMenu?.alpha = 1f
            }
        }

        findViewById<View>(R.id.fabNewTrack).visibility = View.GONE
        findViewById<View>(R.id.fabNewTrack).setOnClickListener {
            fabMenu?.close(false)
            mapFragment?.addNewTrack()
        }
        findViewById<View>(R.id.fabEvent).setOnClickListener {
            fabMenu?.close(false)
            detailFragment?.addEvent()
        }
        findViewById<View>(R.id.fabPictures).setOnClickListener {
            fabMenu?.close(false)
            detailFragment?.doPicturePick()
        }
        findViewById<View>(R.id.fabComment).setOnClickListener {
            fabMenu?.close(false)
            detailFragment?.addRating()
        }

        fabPadding = resources.getDimension(R.dimen.fab_padding).roundToInt()
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        displayHeight = size.y

        MxPreferences.getInstance().edit().putLastOpenStartActivity(this@ActivityMapExtension.javaClass.simpleName).apply()

        val cl = ChangeLog(this)
        if (cl.isFirstRunEver) {
            val settingIntent = Intent(this, ActivitySetting::class.java)
            startActivity(settingIntent)
        } else if (cl.isFirstRun) {
            cl.fullLogDialog.show()
        }
    }

    override fun onBackPressed() {
        if (mapFragment?.onBackPressed()!!) {
            super.onBackPressed()
        }
    }

    fun setFabPosition(panelState: PanelState, headerHeight: Int, position: Float) {
        when (panelState) {
            PanelState.EXPANDED -> Unit
            PanelState.COLLAPSED -> {
                fabMenu?.visibility = View.VISIBLE
                fabMenu?.setPadding(fabPadding, fabPadding, fabPadding, headerHeight + fabPadding)
            }
            PanelState.ANCHORED -> fabMenu?.visibility = View.VISIBLE
            PanelState.HIDDEN -> {
                fabMenu?.visibility = View.GONE
                fabMenu?.setPadding(fabPadding, fabPadding, fabPadding, fabPadding)
            }
            PanelState.DRAGGING -> {
                fabMenu?.close(false)
                fabMenu?.alpha = 1 - position
                fabMenu?.setPadding(
                    fabPadding, fabPadding, fabPadding,
                    Math.round((displayHeight - toolbar.height - headerHeight).toFloat() * position * scalar4Slide) + headerHeight + fabPadding
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
