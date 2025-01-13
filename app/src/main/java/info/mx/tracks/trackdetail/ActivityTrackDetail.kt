package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import android.widget.ListView
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import com.robotoworks.mechanoid.ops.Ops
import info.mx.tracks.R
import info.mx.tracks.base.ActivityDrawerBase
import info.mx.tracks.common.FragmentUpDown.Companion.RECORD_ID_LOCAL
import info.mx.tracks.common.parcelableArrayListExtra
import info.mx.tracks.common.parcelableExtra
import info.mx.tracks.image.AdapterImageUrisAdapter
import info.mx.tracks.ops.AbstractOpPushSharedImageOperation
import info.mx.tracks.sqlite.TracksRecord
import timber.log.Timber
import java.util.*

class ActivityTrackDetail : ActivityDrawerBase(), ImageCursorAdapter.OnImageListItemClick {

    var detailFragmentTab: FragmentTrackDetailTab? = null

    //    FragmentManager.getFragments can only be called from within the same library group (groupId=com.android.support) less... (⌘F1)
    //    This inspection looks at Android API calls that have been annotated with various support annotations (such as RequiresPermission or
    // UiThread) and flags any calls that are not using the API correctly as specified by the annotations.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_detail)

        // Get intent, action and MIME type
        openDetail(intent)

        val menuFab = findViewById<FloatingActionMenu>(R.id.menuFab)
        menuFab.setClosedOnTouchOutside(true)

        findViewById<View>(R.id.fabEvent).setOnClickListener {
            menuFab.close(false)
            this@ActivityTrackDetail.detailFragmentTab?.addEvent()
        }
        findViewById<View>(R.id.fabPictures).setOnClickListener {
            menuFab.close(false)
            this@ActivityTrackDetail.detailFragmentTab?.doPicturePick()
        }
        findViewById<View>(R.id.fabComment).setOnClickListener {
            menuFab.close(false)
            this@ActivityTrackDetail.detailFragmentTab?.addRating()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openDetail(intent)
    }

    override fun onResume() {
        super.onResume()
        // Get intent, action and MIME type
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSendText(intent) // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent) // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
            Timber.d("not handle intent action:$action type:$type")
        }
    }

    private fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            //removes it, to prevent re-share
            intent.removeExtra(Intent.EXTRA_TEXT)

            // Update UI to reflect text being shared
            Timber.d("share %s", sharedText)
            val bundle = getBundlePrepared(intent)

            val track2Share = bundle?.getLong(RECORD_ID_LOCAL)?.let { TracksRecord.get(it) }
            if (track2Share != null) {
                val uris = ArrayList<Uri>()
                uris.add(Uri.parse(sharedText))
                askImportDlg(this, uris, track2Share, true)
            }
        } else {
            Timber.d("no image intent")
        }
    }

    private fun handleSendImage(intent: Intent) {
        val imageUri = intent.parcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Timber.d("share %s", imageUri)
            val bundle = getBundlePrepared(intent)
            val track2Share = bundle?.getLong(RECORD_ID_LOCAL)?.let { TracksRecord.get(it) }
            intent.removeExtra(Intent.EXTRA_TEXT)
            intent.removeExtra("share_screenshot_as_stream")
            val uris = ArrayList<Uri>()
            uris.add(imageUri)
            askImportDlg(this, uris, track2Share, true)
        }
    }

    //    void handleSendImageShare(Intent intent) {
    //        Uri imageUri = intent.getParcelableExtra("share_screenshot_as_stream");
    //        if (imageUri != null) {
    //            // Update UI to reflect image being shared
    //            Bundle bundle = getBundlePrepared(intent);
    //            TracksRecord track2Share = TracksRecord.get(bundle.getLong(FragmentUpDown.Companion.getRECORD_ID_LOCAL()));
    //            intent.removeExtra(Intent.EXTRA_TEXT);
    //            intent.removeExtra("share_screenshot_as_stream");
    //            askImportDlg(this, imageUri.toString(), track2Share);
    //        }
    //    }

    private fun handleSendMultipleImages(intent: Intent) {
        val imageUris = intent.parcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        if (imageUris != null) {
            val bundle = getBundlePrepared(intent)
            val track2Share = bundle?.getLong(RECORD_ID_LOCAL)?.let { TracksRecord.get(it) }
            askImportDlg(this, imageUris, track2Share, true)
        }
    }

    private fun openDetail(intent: Intent) {
        val bundle = getBundlePrepared(intent)
        detailFragmentTab = FragmentTrackDetailTab()
        detailFragmentTab?.arguments = bundle

        // Replace in the fragment_container view with this fragment,
        // add the transaction to the back stack so the user can navigate back
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_detail, detailFragmentTab!!, FragmentTrackDetailTab.TAG)
        // transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_base_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_prev) {
            detailFragmentTab?.moveUp()
        } else if (i == R.id.menu_next) {
            detailFragmentTab?.moveDown()
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("keep it like it is"))
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finish()
    }

    override fun onImageItemClick(position: Int, imageRestId: Long) {
        //TODO what ?
    }

    fun showMessage(text: String, length: Int) {
        val fabMenu = findViewById<FloatingActionMenu>(R.id.menuFab)
        Snackbar.make(fabMenu, text, length).setAction("Action", null).show()
    }

    companion object {

        fun askImportDlg(context: Context, uris: ArrayList<Uri>, track2Share: TracksRecord?, clip: Boolean) {
            if (uris.size == 0 || track2Share == null) {
                return
            }

            var gridView: GridView? = null
            var listView: ListView? = null
            if (uris.size == 1) {
                listView = ListView(context)
                listView.adapter = AdapterImageUrisAdapter(context, uris)
            } else {
                gridView = GridView(context)
                gridView.numColumns = 2
                gridView.adapter = AdapterImageUrisAdapter(context, uris)
            }

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder
                .setTitle(String.format(context.getString(R.string.import_image), track2Share.trackname))
                .setView(if (uris.size == 1) listView else gridView)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    for (uri in uris) {
                        val intentM = AbstractOpPushSharedImageOperation.newIntent(track2Share.restId, uri.toString())
                        Ops.execute(intentM)
                        if (clip) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val data = ClipData.newPlainText("", "")
                            clipboard.setPrimaryClip(data)
                        }
                    }
                }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.window!!.setLayout(AdapterImageUrisAdapter.getDesiredScreenWidth(), AdapterImageUrisAdapter.getDesiredScreenHeight())
            alertDialog.show()
        }
    }
}
