package info.hannes.mxadmin.location

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import info.hannes.mxadmin.base.ActivityAdminBase
import info.mx.tracks.R
import info.mx.tracks.room.MxDatabase
import info.mx.tracks.tools.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class ActivityLocationMonitor : ActivityAdminBase() {

    private var adapterMonitored: AdapterLocationMonitor? = null

    val mxDatabase: MxDatabase by inject()

    val permissionHelper: PermissionHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_monitor)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            // onBackPressed()
            finish()
        }

        adapterMonitored = AdapterLocationMonitor(this)

        val listLocation = findViewById<ListView>(R.id.listLocation)
        listLocation.adapter = adapterMonitored
    }

    public override fun onResume() {
        super.onResume()
        // sort by distance
        addDisposable(mxDatabase.capturedLatLngDao()
            .all
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { locations -> adapterMonitored!!.setLocations(locations) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_filter_country, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        menu.findItem(R.id.action_settings_filter_country).icon = if (permissionHelper.hasLocationPermission()) {
            ContextCompat.getDrawable(this, R.drawable.actionbar_checkbox)
        } else {
            ContextCompat.getDrawable(this, R.drawable.actionbar_checkbox_empty)
        }
        return super.onPrepareOptionsMenu(menu)
    }

}
