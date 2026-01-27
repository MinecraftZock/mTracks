package info.mx.tracks.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import info.hannes.commonlib.LocationHelper
import info.hannes.commonlib.dialog.BackDialog
import info.mx.tracks.ActivityBase
import info.mx.tracks.R
import info.mx.tracks.common.FragmentUpDown
import info.mx.tracks.common.SecHelper
import info.mx.tracks.map.ActivityMapExtension
import info.mx.tracks.navigation.AppNavigationMenu
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.settings.ActivityFilter
import info.mx.tracks.settings.ActivitySetting
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord
import info.mx.tracks.trackdetail.ActivityTrackDetail
import info.mx.tracks.trackdetail.ActivityTrackEdit
import info.mx.tracks.tracklist.ActivityTrackList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class ActivityDrawerBase : ActivityBase(), NavigationView.OnNavigationItemSelectedListener, KoinComponent {

    private var drawerToggle: ActionBarDrawerToggle? = null
    private lateinit var drawerLayout: DrawerLayout

    private val appNavigationMenu: AppNavigationMenu by inject()

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initToolbar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    /**
     * Show back confirm dialog.
     */
    private fun showBackDialog() {
        val fm = supportFragmentManager
        val editNameDialog = BackDialog()
        editNameDialog.show(fm, BACK)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        return if (itemId == android.R.id.home) {
            showBackDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar() {
        drawerLayout = findViewById(R.id.drawer_layout)

        initHeaderView()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationView = drawerLayout.findViewById<NavigationView>(R.id.navigation_view)
        //enable color icons
        navigationView.itemIconTintList = null
        //hide for track edit
        navigationView.menu.setGroupVisible(R.id.drawer_separator_track, javaClass == ActivityTrackDetail::class.java)
        navigationView.menu.findItem(R.id.drawer_filter).isVisible = javaClass != ActivityTrackDetail::class.java
        navigationView.menu.findItem(R.id.drawer_tracks).isVisible = javaClass != ActivityTrackList::class.java
        navigationView.menu.findItem(R.id.drawer_map).isVisible = javaClass != ActivityMapExtension::class.java
        navigationView.menu.findItem(R.id.drawer_navigation).isVisible = permissionHelper.hasLocationPermission() && this is ActivityTrackDetail

        navigationView.setNavigationItemSelectedListener(this)
        appNavigationMenu.addFlavorItems(navigationView)

        // set up the hamburger icon to open and close the drawer
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle!!)
        drawerToggle!!.syncState()

        // Setup back press handling using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers()
                } else {
                    showBackDialog()
                }
            }
        })
    }

    /**
     * http://stackoverflow.com/questions/33364276/getting-error-in-existing-code-after-updating-support-repository-to-23-1-0
     */
    private fun initHeaderView() {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val header = navigationView.inflateHeaderView(R.layout.drawer_header)
        val textUserName = header.findViewById<TextView>(R.id.textUserName)
        val mxPrefs = MxPreferences.getInstance()
        // trim if necessary
        if (mxPrefs.username.trim { it <= ' ' } != mxPrefs.username) {
            mxPrefs.edit().putUsername(mxPrefs.username.trim { it <= ' ' }).apply()
        }
        textUserName.text = mxPrefs.username

        val avatarView = header.findViewById<ImageView>(R.id.avatar)
        avatarView.setImageResource(R.drawable.actionbar_logo)
    }

    public override fun onPause() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
        super.onPause()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var result = false
        if (menuItem.itemId == R.id.drawer_tracks) {
            val qWfIntent = Intent(this, ActivityTrackList::class.java)
            startActivity(qWfIntent)
            result = true
            finish()
        } else if (menuItem.itemId == R.id.drawer_map) {
            val qWfIntent = Intent(this, ActivityMapExtension::class.java)
            startActivity(qWfIntent)
            result = true
            finish()
        } else if (menuItem.itemId == R.id.drawer_filter) {
            val qWfIntent = Intent(this, ActivityFilter::class.java)
            startActivity(qWfIntent)
            result = true
        } else if (menuItem.itemId == R.id.drawer_settings) {
            val qWfIntent = Intent(this, ActivitySetting::class.java)
            startActivity(qWfIntent)
            result = true
        } else if (menuItem.itemId == R.id.drawer_feedback) {
            doSendFeedBackMail()
            result = true
        } else if (menuItem.itemId == R.id.drawer_facebook) {
            startActivity(openFacebookIntent)
            result = true
        } else if (menuItem.itemId == R.id.drawer_new) {
            val qWfIntent = Intent(this, ActivityTrackEdit::class.java)
            startActivity(qWfIntent)
            result = true
        } else if (menuItem.itemId == R.id.drawer_edit) {
            if (this is ActivityTrackDetail) {
                val qWfIntent = Intent(this, ActivityTrackEdit::class.java)
                val bundle = Bundle()
                bundle.putString(FragmentUpDown.CONTENT_URI, MxInfoDBContract.Tracksges.CONTENT_URI.toString())
                bundle.putLong(FragmentUpDown.RECORD_ID_LOCAL, this.detailFragmentTab!!.recordLocalId) // FIXME
                qWfIntent.putExtras(bundle)
                startActivity(qWfIntent)
                result = true
            }
        } else if (menuItem.itemId == R.id.drawer_track_feedback) {
            if (this is ActivityTrackDetail) {
                val record = TracksRecord.get(this.detailFragmentTab!!.recordLocalId)
                if (record != null) {
                    doFeedBackDialog(record.trackname)
                }
                result = true
            }
        } else if (menuItem.itemId == R.id.drawer_navigation) {
            if (this is ActivityTrackDetail) {
                val record = TracksRecord.get(this.detailFragmentTab!!.recordLocalId)
                if (record != null) {
                    LocationHelper.openNavi(
                        this, null,
                        SecHelper.entcryptXtude(record.latitude),
                        SecHelper.entcryptXtude(record.longitude)
                    )
                }
                result = true
            }
        }

        if (!result) {
            appNavigationMenu.handleFlavorItem(this, menuItem)
        }
        return result
    }

    private fun doSendFeedBackMail() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("appdev.droider@gmail.com"))
        i.putExtra(Intent.EXTRA_SUBJECT, "MX Tracks Info feedback")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (_: android.content.ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_email_installed), Toast.LENGTH_SHORT).show()
        }

    }

    private fun doSendTrackMail(name: String, desc: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf("appdev.droider@gmail.com"))
        i.putExtra(Intent.EXTRA_SUBJECT, "MX Tracks Info / track error")
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.name) + ":" + name + "\n" + getString(R.string.note) + ":" + desc)
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (_: android.content.ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_email_installed), Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("InflateParams")
    private fun doFeedBackDialog(trackName: String) {
        val builder = AlertDialog.Builder(this)

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        val layout: View = inflater.inflate(R.layout.dialog_newtrack, null)
        val edtName = layout.findViewById<EditText>(R.id.newtrack_name)
        val edtDesc = layout.findViewById<EditText>(R.id.newtrack_description)
        edtName.setText(trackName)
        builder.setTitle(getString(R.string.track_error))
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            doSendTrackMail(
                edtName.text.toString(), edtDesc.text.toString()
            )
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.setView(layout)
        dialog.show()
    }

    companion object {

        private const val BACK = "back"

        //        try {
        //            context.getPackageManager().getPackageInfoCompat("com.facebook.katana", 0);
        //            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://groups/1435212740065304"));
        //        } catch (Exception e) {
        //            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/1435212740065304"));
        //        }
        //        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/1435212740065304"));
        val openFacebookIntent: Intent
            get() = Intent(Intent.ACTION_VIEW, "https://www.facebook.com/pages/MX-Tracks-Info/1492727287710863".toUri())
    }
}
