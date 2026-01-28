package info.mx.tracks.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import info.mx.tracks.R
import info.mx.tracks.base.ActivityRx
import info.mx.tracks.databinding.ActivityFilterCountryBinding
import info.mx.core_generated.prefs.MxPreferences

class ActivityFilterCountry : ActivityRx() {

    private lateinit var binding: ActivityFilterCountryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterCountryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
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
    }

    override fun onPause() {
        super.onPause()
        // force a refresh of markers in map
        MxPreferences.getInstance().edit().putSearchOpenDi(MxPreferences.getInstance().searchOpenDi)
            .commit()
    }
}
