package info.mx.tracks.tracklist

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import info.hannes.changelog.ChangeLog
import info.hannes.commonlib.utils.setPhoneHasNoOptionsBtn
import info.mx.tracks.MxApplication
import info.mx.tracks.R
import info.mx.tracks.base.ActivityDrawerBase
import info.mx.tracks.common.FragmentEmpty
import info.mx.tracks.common.FragmentUpDown.Companion.CONTENT_URI
import info.mx.tracks.common.FragmentUpDown.Companion.RECORD_ID_LOCAL
import info.mx.tracks.databinding.ActivityTracksListBinding
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.service.LocationJobService
import info.mx.tracks.settings.ActivitySetting
import info.mx.tracks.sqlite.MxInfoDBContract.Tracksges
import info.mx.tracks.tools.PermissionHelper
import info.mx.tracks.trackdetail.ActivityTrackDetail
import info.mx.tracks.trackdetail.ActivityTrackEdit
import info.mx.tracks.trackdetail.FragmentTrackDetail
import info.mx.tracks.trackdetail.FragmentTrackDetailTab

class ActivityTrackList : ActivityDrawerBase(), FragmentTrackList.Callbacks, CallbacksTabs {

    /*
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private var twoPane: Boolean = false

    private val detailTagFragment: FragmentTrackDetailTab by lazy { supportFragmentManager.findFragmentByTag(FragmentTrackDetailTab.TAG) as FragmentTrackDetailTab }

    private lateinit var fragmentTrackListTab: FragmentTrackListTab

    private lateinit var binding: ActivityTracksListBinding

    override fun onResume() {
        super.onResume()
        if (findViewById<View>(R.id.track_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the activity should be in two-pane mode.
            twoPane = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracksListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        fragmentTrackListTab = supportFragmentManager.findFragmentByTag(FragmentTrackListTab.TAG) as FragmentTrackListTab

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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

        binding.fabSingle.setOnClickListener {
            val intentAddEdit = Intent(this@ActivityTrackList, ActivityTrackEdit::class.java)
            this@ActivityTrackList.startActivity(intentAddEdit)
        }
        binding.fabLayout.menuFab.setClosedOnTouchOutside(true)

        findViewById<View>(R.id.fabEvent).setOnClickListener {
            binding.fabLayout.menuFab.close(false)
            this@ActivityTrackList.detailTagFragment.addEvent()
        }
        findViewById<View>(R.id.fabPictures).setOnClickListener {
            binding.fabLayout.menuFab.close(false)
            this@ActivityTrackList.detailTagFragment.doPicturePick()
        }
        findViewById<View>(R.id.fabComment).setOnClickListener {
            binding.fabLayout.menuFab.close(false)
            this@ActivityTrackList.detailTagFragment.addRating()
        }

        this.setPhoneHasNoOptionsBtn()
        MxPreferences.getInstance().edit().putLastOpenStartActivity(this@ActivityTrackList.javaClass.simpleName).apply()

        val changeLog = ChangeLog(this)
        if (MxApplication.isRunningEspresso()) {
            Unit
        } else if (changeLog.isFirstRunEver) {
            val settingIntent = Intent(this, ActivitySetting::class.java)
            startActivity(settingIntent)
        } else if (changeLog.isFirstRun) {
            changeLog.fullLogDialog.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var granted = false
        when (requestCode) {
            PermissionHelper.REQUEST_PERMISSION_LOCATION -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED && permissions[i].endsWith("LOCATION")) {
                        granted = true
                    }
                }
                if (granted) {
                    // We can now safely use the API we requested access to
                    LocationJobService.restartService(this)
                } else {
                    // Permission was denied or request was cancelled; currently do nothing, no dialog..
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Display UI and wait for user interaction
                        showMessageOKCancel { _, _ -> this@ActivityTrackList.openPermission() }
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                            PermissionHelper.REQUEST_PERMISSION_LOCATION
                        )
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(onClickListener: DialogInterface.OnClickListener) {
        val myAlertDialog = AlertDialog.Builder(this)
        myAlertDialog.setTitle(R.string.info)
        myAlertDialog.setMessage(R.string.LOCATION_EXPLAIN_AND_RETHINK_ABOUT)
        myAlertDialog.setPositiveButton(R.string.yes, onClickListener)
        myAlertDialog.setNegativeButton(android.R.string.cancel) { _, _ ->
            // do something when the Cancel button is clicked
        }
        myAlertDialog.show()
    }

    /**
     * Callback method from [FragmentTrackList.Callbacks] indicating that the item with the given ID was selected.
     */
    override fun onItemSelected(id: Long) {
        if (twoPane) {
            binding.fabSingle.visibility = View.GONE
            binding.fabLayout.menuFab.visibility = View.VISIBLE
            // In two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
            val arguments = Bundle()
            arguments.putLong(RECORD_ID_LOCAL, id)
            arguments.putString(CONTENT_URI, Tracksges.CONTENT_URI.toString())
            arguments.putBoolean(FragmentTrackDetail.IN_SLIDER, false)
            val fragment = FragmentTrackDetailTab()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .replace(R.id.track_detail_container, fragment, FragmentTrackDetailTab.TAG)
                .commit()
        } else {
            // In single-pane mode, simply start the detail activity for the selected item ID.
            val detailIntent = Intent(this, ActivityTrackDetail::class.java)
            //FIXME
            detailIntent.putExtra(RECORD_ID_LOCAL, id)
            startActivity(detailIntent)
        }
    }

    override fun onTabPageSelected(id: Long) {
        // In single-pane mode, simply start the detail activity for the selected item ID.
        // -> do nothing
        if (twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction.
            val fragment = FragmentEmpty()
            supportFragmentManager.beginTransaction()
                .replace(R.id.track_detail_container, fragment)
                .commit()
        }
    }
}
