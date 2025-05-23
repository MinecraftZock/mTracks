package info.hannes.mxadmin.download

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, Intent(this, ActivityDownloadList::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}