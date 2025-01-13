package info.mx.tracks.trackdetail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import info.hannes.commonlib.DialogHelper
import info.hannes.commonlib.DialogHelper.doAskYesNo
import info.mx.tracks.ActivityBase
import info.mx.tracks.R

class ActivityTrackEdit : ActivityBase() {

    private var fragmentTrackEdit: FragmentTrackEdit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_edit)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        openEdit()
    }

    private fun openEdit() {
        fragmentTrackEdit = FragmentTrackEdit()

        // Replace in the fragment_container view with this fragmentTrackEdit,
        // add the transaction to the back stack so the user can navigate back
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragmentTrackEdit!!)
        // transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (fragmentTrackEdit!!.mask2Record(false)) {
                    var txt = getString(R.string.ask_unsaved_changes)
                    if (fragmentTrackEdit!!.hasDefaultLatLon()) {
                        txt = getString(R.string.default_latlon) + "/n/n" + txt
                    }
                    doAskYesNo(this, 0, R.string.unsaved_changes, txt, SaveStage(), CloseStage())
                    return false
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (fragmentTrackEdit != null && fragmentTrackEdit!!.mask2Record(false)) {
            var txt = getString(R.string.ask_unsaved_changes)
            if (fragmentTrackEdit!!.hasDefaultLatLon()) {
                txt = """
                    (${getString(R.string.default_latlon)})
                    
                    $txt
                    """.trimIndent()
            }
            doAskYesNo(this, 0, R.string.unsaved_changes, txt, SaveStage(), CloseStage())
        } else {
            super.onBackPressed()
        }
    }

    private inner class SaveStage : DialogHelper.Callable {
        override fun execute(param: Long) {
            fragmentTrackEdit!!.mask2Record(true)
            finish()
        }
    }

    private inner class CloseStage : DialogHelper.Callable {
        override fun execute(param: Long) {
            finish()
        }
    }
}