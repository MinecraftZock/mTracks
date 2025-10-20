package info.mx.tracks.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import info.hannes.changelog.ChangeLog
import info.hannes.commonlib.TrackingApplication.Companion.getVersionName
import info.mx.tracks.ActivityBase
import info.mx.tracks.BuildConfig
import info.mx.tracks.MxCoreApplication.Companion.readSettings
import info.mx.tracks.MxCoreApplication.Companion.trackEvent
import info.mx.tracks.R
import info.mx.tracks.databinding.ActivitySettingBinding
import info.mx.tracks.prefs.MxPreferences

class ActivitySetting : ActivityBase() {

    private lateinit var prefs: MxPreferences

    private lateinit var binding: ActivitySettingBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        prefs = MxPreferences.getInstance()
        binding.containerSetting.layoutLicense.setOnClickListener {
            startActivity(Intent(this@ActivitySetting, ActivityAcknowledgement::class.java))
        }
        val layoutChangeLog = findViewById<LinearLayout>(R.id.layoutChangeLog)
        layoutChangeLog.setOnClickListener {
            ChangeLog(this@ActivitySetting).fullLogDialog.show()
        }
        val chkSurveillance = findViewById<CheckBox>(R.id.setting_surveillance)
        chkSurveillance.isChecked = prefs.agreeTrackSurveillance
        chkSurveillance.alpha = if (prefs.agreeTrackSurveillance) 1F else 0.8f
        chkSurveillance.setOnClickListener { view: View ->
            prefs.edit().putAgreeTrackSurveillance((view as CheckBox).isChecked).commit()
            chkSurveillance.alpha = if (prefs.agreeTrackSurveillance) 1F else 0.8f
            readSettings()
        }
        binding.containerSetting.settingTracking.isChecked = prefs.agreeTracking
        binding.containerSetting.settingTracking.alpha = if (prefs.agreeTracking) 1F else 0.8f
        binding.containerSetting.settingTracking.setOnCheckedChangeListener { view: CompoundButton, _: Boolean ->
            if (BuildConfig.FLAVOR.equals("free", ignoreCase = true)) {
                binding.containerSetting.settingTracking.isChecked = prefs.agreeTracking // revert change
                showProVersionDialog()
            } else {
                prefs.edit().putAgreeTracking(view.isChecked).commit()
                binding.containerSetting.settingTracking.alpha = if (prefs.agreeTracking) 1F else 0.8f
                readSettings()
            }
        }
        binding.containerSetting.settingUsername.setText(prefs.username)
        binding.containerSetting.settingUsername.setOnFocusChangeListener { view: View, _: Boolean ->
            prefs.edit().putUsername((view as EditText).editableText.toString()).commit()
        }
        binding.containerSetting.setToggleKm.isChecked = prefs.unitsKm
        binding.containerSetting.setToggleKm.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            prefs.edit().putUnitsKm(isChecked).commit()
        }
        (findViewById<View>(R.id.setting_version) as TextView).text = getVersionName(this)
        hideKeyboard()
    }

    private fun hideKeyboard() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onPause() {
        super.onPause()
        trackEvent("Setting", "Tracking:" + prefs.agreeTracking)
        trackEvent("Setting", "Improve:" + prefs.agreeTrackSurveillance)
    }

    override fun onBackPressed() {
        prefs.edit().putUsername(binding.containerSetting.settingUsername.editableText.toString()).commit()
        super.onBackPressed()
    }
}