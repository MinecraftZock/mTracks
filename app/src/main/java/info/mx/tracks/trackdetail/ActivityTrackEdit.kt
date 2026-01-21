package info.mx.tracks.trackdetail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import info.hannes.commonlib.DialogHelper
import info.hannes.commonlib.DialogHelper.doAskYesNo
import info.mx.tracks.ActivityBase
import info.mx.tracks.R

class ActivityTrackEdit : ActivityBase() {

    private var fragmentTrackEdit: FragmentTrackEdit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_edit)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Setup back press handling using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (fragmentTrackEdit != null && fragmentTrackEdit!!.mask2Record(false)) {
                    var txt = getString(R.string.ask_unsaved_changes)
                    if (fragmentTrackEdit!!.hasDefaultLatLon()) {
                        txt = """
                            (${getString(R.string.default_latlon)})
                            
                            $txt
                            """.trimIndent()
                    }
                    doAskYesNo(this@ActivityTrackEdit, 0, R.string.unsaved_changes, txt, SaveStage(), CloseStage())
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })


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