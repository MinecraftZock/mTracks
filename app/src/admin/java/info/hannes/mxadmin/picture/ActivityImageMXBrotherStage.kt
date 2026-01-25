package info.hannes.mxadmin.picture

import android.database.Cursor
import android.view.Menu
import android.view.MenuItem
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mechadminGen.sqlite.MxAdminDBContract
import info.hannes.mechadminGen.sqlite.PictureStageRecord
import info.hannes.mechadminGen.sqlite.TrackstageBrotherRecord
import info.mx.tracks.R
import info.mx.tracks.prefs.MxPreferences
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.TracksRecord
import timber.log.Timber

class ActivityImageMXBrotherStage : ActivityBaseImageStageSlider() {
    private var currPic: PictureStageRecord? = null
    private var showOnlyInteresting = true
    override val picturesQuery: SQuery
        get() {
            return SQuery.newQuery()
                .expr(
                    MxAdminDBContract.PictureStage.UNINTERESSANT,
                    SQuery.Op.EQ,
                    if (showOnlyInteresting) 0 else 1
                )
                .append(MxAdminDBContract.PictureStage.REST_ID + " is null")
        }

    override fun onPause() {
        if (currPic != null) {
            val trackBrother = TrackstageBrotherRecord.get(currPic!!.trackId)
            val track = SQuery.newQuery()
                .expr(MxInfoDBContract.Tracks.REST_ID, SQuery.Op.EQ, trackBrother.trackRestId)
                .selectFirst<TracksRecord>(MxInfoDBContract.Tracks.CONTENT_URI)
            if (track != null) {
                MxPreferences.getInstance().edit().putRestoreID(track.id).commit()
                Timber.d("set current Track: %s", track.trackname)
            }
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_image_confirm, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_image_confirm).isVisible = false
        val showAllItem = menu.findItem(R.id.menu_image_show_all)
        showAllItem.isChecked = showOnlyInteresting
        showAllItem.title = if (showAllItem.isChecked) "interesting" else "not interesting"
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_image_confirm) {
        } else if (i == R.id.menu_image_show_all) {
            showOnlyInteresting = !showOnlyInteresting
            restartThumbsLoader()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLoadFinished(loader: Loader<Cursor?>, cursor: Cursor?) {
        super.onLoadFinished(loader, cursor)
        when (loader.id) {
            LOADER_PICTURE_THUMBS -> {
                this.title = "Picture confirm ${cursor?.count}"
                if (currPic != null) {
                    currPic!!.reload()
                }
                invalidateOptionsMenu()
            }
        }
    }

}
