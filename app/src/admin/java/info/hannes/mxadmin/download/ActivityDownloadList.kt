package info.hannes.mxadmin.download

import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.widget.Toolbar

import info.mx.tracks.base.ActivityRx
import info.mx.tracks.R

/**
 * An activity representing a list of download items. This activity has different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a [ActivityDownloadDetail] representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical panes.
 *
 *
 * The activity makes heavy use of fragments. The list of items is a [FragmentDownloadList] and the item details (if present) is a
 * [FragmentDownloadDetail].
 *
 *
 * This activity also implements the required [FragmentDownloadList.Callbacks] interface to listen for item selections.
 */
class ActivityDownloadList : ActivityRx(), FragmentDownloadList.Callbacks {

    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaditem_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            this@ActivityDownloadList.finish()
        }

        if (findViewById<View>(R.id.downloaditem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            (supportFragmentManager
                    .findFragmentById(R.id.downloaditem_list) as FragmentDownloadList)
                    .setActivateOnItemClick(true)
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from [FragmentDownloadList.Callbacks] indicating that the item with the given ID was selected.
     */
    override fun onItemSelected(id: Long) {
        if (twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            val arguments = Bundle()
            arguments.putLong(FragmentDownloadDetail.ARG_ITEM_ID, id)
            val fragment = FragmentDownloadDetail()
            fragment.arguments = arguments
            supportFragmentManager.beginTransaction()
                    .replace(R.id.downloaditem_detail_container, fragment)
                    .commit()

        } else {
            // In single-pane mode, simply start the detail activity for the selected item ID.
            val detailIntent = Intent(this, ActivityDownloadDetail::class.java)
            detailIntent.putExtra(FragmentDownloadDetail.ARG_ITEM_ID, id)
            startActivity(detailIntent)
        }
    }

}
