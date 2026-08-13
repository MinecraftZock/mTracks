package info.hannes.mxadmin.download

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import info.mx.tracks.R
import info.mx.tracks.base.ActivityRx

/**
 * An activity representing a single download item detail screen. This activity is only used on handset devices. On tablet-size devices, item details
 * are presented side-by-side with a list of items in a [ActivityDownloadList].
 *
 *
 * This activity is mostly just a 'shell' activity containing nothing more than a [FragmentDownloadDetail].
 */
class ActivityDownloadDetail : ActivityRx() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaditem_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val arguments = Bundle()
            arguments.putLong(
                FragmentDownloadDetail.ARG_ITEM_ID,
                intent.getLongExtra(FragmentDownloadDetail.ARG_ITEM_ID, -1)
            )
            val fragment = FragmentDownloadDetail()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                .add(R.id.downloaditem_detail_container, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        // Setup menu with modern MenuProvider API
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: android.view.Menu, menuInflater: android.view.MenuInflater) {
                // Menu creation handled by base class or not needed
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        finish()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.STARTED)
    }
}
