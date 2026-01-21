package info.mx.tracks.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.commonlib.DateHelper
import info.mx.tracks.ActivityBase
import info.mx.tracks.MxCoreApplication.Companion.isAdmin
import info.mx.tracks.R
import info.mx.tracks.adapter.ArrayAdapterRightAlign
import info.mx.tracks.common.QueryHelper
import info.mx.tracks.common.setDayLayout
import info.mx.tracks.databinding.ActivityFilterBinding
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.service.LocationJobService.Companion.restartService
import info.mx.tracks.sqlite.AbstractMxInfoDBOpenHelper
import info.mx.tracks.sqlite.CountryRecord
import info.mx.tracks.sqlite.MxInfoDBContract.Country
import info.mx.tracks.sqlite.MxInfoDBContract.Tracksges
import timber.log.Timber

class ActivityFilter : ActivityBase() {

    private lateinit var binding: ActivityFilterBinding

    private lateinit var prefs: MxPreferences

    private var handlerErrorHandler = Handler(Looper.getMainLooper())
    private var runnableErrorHide = Runnable { binding.include.tvFilterCount.error = null }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

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

        prefs = MxPreferences.getInstance()
        val spinnerAdapter = ArrayAdapterRightAlign.createFromResource(this, R.array.location_list, R.layout.spinner_right)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_right)
        binding.include.spinnerLocation.adapter = spinnerAdapter
        val soilAdapter = ArrayAdapterRightAlign.createFromResource(this, R.array.soil_list, R.layout.spinner_right)
        soilAdapter.setDropDownViewResource(R.layout.spinner_item_right)
        binding.include.spinnerSoil.adapter = soilAdapter
        binding.include.lyAdminOnly.visibility = if (isAdmin) View.VISIBLE else View.GONE

        binding.include.tvFilterCount.setOnClickListener { binding.include.tvFilterCount.error = null }
        binding.include.tvFilterCountGes.setOnClickListener { binding.include.tvFilterCount.error = null }
        binding.include.lyCountries.setOnClickListener {
            val qWfIntent = Intent(this@ActivityFilter, ActivityFilterCountry::class.java)
            startActivity(qWfIntent)
        }
        val shortWeekdays = DateHelper.shortWeekdays
        binding.include.tvFilterMo.text = shortWeekdays[2]
        binding.include.tvFilterDi.text = shortWeekdays[3]
        binding.include.tvFilterMi.text = shortWeekdays[4]
        binding.include.tvFilterDo.text = shortWeekdays[5]
        binding.include.tvFilterFr.text = shortWeekdays[6]
        binding.include.tvFilterSa.text = shortWeekdays[7]
        binding.include.tvFilterSo.text = shortWeekdays[1]
        binding.include.tvFilterMo.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenMo((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterDi.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenDi((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterMi.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenMi((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterDo.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenDo((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterFr.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenFr((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterSa.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenSa((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }
        binding.include.tvFilterSo.setOnClickListener { v: View ->
            v.tag = !(v.tag as Boolean)
            prefs.edit().putSearchOpenSo((v.tag as Boolean)).commit()
            v.setDayLayout(v.tag as Boolean)
            setTrackCount()
        }

        // TODO temp entfernt, group by geht nicht
        // chkPicture.setVisibility(View.GONE);
        binding.include.filterRatingBar.isFocusableInTouchMode = true
        binding.include.filterRatingBar.isClickable = true
        binding.include.filterRatingBar.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar: RatingBar, _: Float, _: Boolean ->
            prefs.edit().putShowRating(ratingBar.rating).commit()
            setTrackCount()
        }
        binding.include.filterDifficult.isFocusableInTouchMode = true
        binding.include.filterDifficult.isClickable = true
        binding.include.filterDifficult.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar: RatingBar, _: Float, _: Boolean ->
            prefs.edit().putShowDifficult(ratingBar.rating).commit()
            setTrackCount()
        }
        binding.include.filterOnlyOpen.setOnClickListener { view: View ->
            prefs.edit().putOnlyOpen((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterOnlyApproved.setOnClickListener { view: View ->
            prefs.edit().putOnlyApproved((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowmx.setOnClickListener { view: View ->
            prefs.edit().putShowMx((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowquad.setOnClickListener { view: View ->
            prefs.edit().putShowQuad((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowutv.setOnClickListener { view: View ->
            prefs.edit().putShowUtv((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShow4x4.setOnClickListener { view: View ->
            prefs.edit().putShow4x4((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowEnduro.setOnClickListener { view: View ->
            prefs.edit().putShowEnduro((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowmx.setOnClickListener { view: View ->
            prefs.edit().putShowMx((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterOnlywithkids.setOnClickListener { view: View ->
            prefs.edit().putOnlyWithKids((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowdealers.setOnClickListener { view: View ->
            prefs.edit().putShowDealers((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShoweveryone.setOnClickListener { view: View ->
            prefs.edit().putShowEveryone((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowmember.setOnClickListener { view: View ->
            prefs.edit().putShowMember((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShowrace.setOnClickListener { view: View ->
            prefs.edit().putShowRace((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterSupercross.setOnClickListener { view: View ->
            prefs.edit().putSearchSuperCross((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterShower.setOnClickListener { view: View ->
            prefs.edit().putSearchShower((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterWash.setOnClickListener { view: View ->
            prefs.edit().putSearchWash((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterCamping.setOnClickListener { view: View ->
            prefs.edit().putSearchCamping((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterElectricity.setOnClickListener { view: View ->
            prefs.edit().putSearchElectricity((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.filterPicture.setOnClickListener { view: View ->
            prefs.edit().putSearchPicture((view as CheckBox).isChecked).commit()
            setTrackCount()
        }
        binding.include.spinnerDebugAnsicht.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                prefs.edit().putDebugTrackAnsicht(position - 1).commit()
                setTrackCount()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.include.spinnerSoil.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                prefs.edit().putSoilView(position - 1).commit()
                setTrackCount()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.include.spinnerLocation.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                prefs.edit().putLocationView(position - 1).commit()
                setTrackCount()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        setGui()
    }

    override fun onPause() {
        super.onPause()
        restartService(this.applicationContext)
    }

    private fun setGui() {
        binding.include.spinnerDebugAnsicht.setSelection(prefs.debugTrackAnsicht + 1)
        binding.include.spinnerLocation.setSelection(prefs.locationView + 1)
        binding.include.spinnerSoil.setSelection(prefs.soilView + 1)
        binding.include.tvFilterMo.tag = prefs.searchOpenMo
        binding.include.tvFilterDi.tag = prefs.searchOpenDi
        binding.include.tvFilterMi.tag = prefs.searchOpenMi
        binding.include.tvFilterDo.tag = prefs.searchOpenDo
        binding.include.tvFilterFr.tag = prefs.searchOpenFr
        binding.include.tvFilterSa.tag = prefs.searchOpenSa
        binding.include.tvFilterSo.tag = prefs.searchOpenSo
        binding.include.tvFilterMo.setDayLayout(prefs.searchOpenMo)
        binding.include.tvFilterDi.setDayLayout(prefs.searchOpenDi)
        binding.include.tvFilterMi.setDayLayout(prefs.searchOpenMi)
        binding.include.tvFilterDo.setDayLayout(prefs.searchOpenDo)
        binding.include.tvFilterFr.setDayLayout(prefs.searchOpenFr)
        binding.include.tvFilterSa.setDayLayout(prefs.searchOpenSa)
        binding.include.tvFilterSo.setDayLayout(prefs.searchOpenSo)
        binding.include.filterOnlyOpen.isChecked = prefs.onlyOpen
        Timber.d("prefs.onlyOpen=${prefs.onlyOpen}")
        binding.include.filterOnlyApproved.isChecked = prefs.onlyApproved
        binding.include.filterShowmx.isChecked = prefs.showMx
        binding.include.filterShowquad.isChecked = prefs.showQuad
        binding.include.filterShowutv.isChecked = prefs.showUtv
        binding.include.filterShow4x4.isChecked = prefs.show4x4
        binding.include.filterShowEnduro.isChecked = prefs.showEnduro
        binding.include.filterOnlywithkids.isChecked = prefs.onlyWithKids
        binding.include.filterSupercross.isChecked = prefs.searchSuperCross
        binding.include.filterShower.isChecked = prefs.searchShower
        binding.include.filterWash.isChecked = prefs.searchWash
        binding.include.filterCamping.isChecked = prefs.searchCamping
        binding.include.filterElectricity.isChecked = prefs.searchElectricity
        binding.include.filterPicture.isChecked = prefs.searchPicture
        binding.include.filterShowdealers.isChecked = prefs.showDealers
        binding.include.filterShoweveryone.isChecked = prefs.showEveryone
        binding.include.filterShowmember.isChecked = prefs.showMember
        binding.include.filterShowrace.isChecked = prefs.showRace
        binding.include.filterRatingBar.rating = prefs.showRating
        binding.include.filterDifficult.rating = prefs.showDifficult
        setTrackCount()
        setCountryList()
    }

    private fun setCountryList() {
        var res = StringBuilder()
        val countries = SQuery.newQuery().expr(Country.SHOW, SQuery.Op.EQ, 1).select<CountryRecord>(Country.CONTENT_URI)
        for (record in countries) {
            res.append(record.country).append(",")
        }
        if (res.isNotEmpty()) {
            res = StringBuilder(res.substring(0, res.length - 1))
        }
        binding.include.tvCountryList.text = if (res.toString() == "") getString(R.string.no_country_selected) else res.toString()
    }

    private fun setTrackCount() {
        var queryGes = SQuery.newQuery()
        queryGes = QueryHelper.buildTracksFilterGes(queryGes)
        val countRecGes = queryGes.count(Tracksges.CONTENT_URI)
        binding.include.tvFilterCountGes.text = String.format(getString(R.string.tracksfoundges), countRecGes.toString() + "")
        var query = SQuery.newQuery()
        query = QueryHelper.buildTracksFilter(query, AbstractMxInfoDBOpenHelper.Sources.TRACKSGES)
        val countRecords = query.count(Tracksges.CONTENT_URI)
        binding.include.tvFilterCount.text = String.format(getString(R.string.tracksfound), countRecords.toString() + "")
        if (countRecords > 1000) {
            binding.include.tvFilterCount.error = getString(R.string.many_records)
            handlerErrorHandler.removeCallbacks(runnableErrorHide)
            handlerErrorHandler.postDelayed(runnableErrorHide, WAIT_TO_HIDE)
        } else {
            binding.include.tvFilterCount.error = null
        }
        binding.include.tvFilterCount.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var res: Boolean = super.onOptionsItemSelected(item)
        if (item.itemId == R.id.menu_reset) {
            QueryHelper.resetFilter()
            setGui()
            res = true
        }
        return res
    }

    companion object {
        private const val WAIT_TO_HIDE: Long = 7000
    }
}