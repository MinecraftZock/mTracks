package info.hannes.mxadmin.picture

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.Settings.Secure
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.loader.content.Loader
import com.robotoworks.mechanoid.db.SQuery
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.comAdminlib.retrofit.service.model.ApproveResponse
import info.mx.comAdminlib.retrofit.service.model.Approved
import info.mx.comlib.retrofit.service.data.BaseSingleObserver
import info.mx.tracks.R
import info.mx.tracks.image.ActivityBaseImageSlider
import info.mx.tracks.sqlite.MxInfoDBContract
import info.mx.tracks.sqlite.PicturesRecord
import org.koin.android.ext.android.inject

class ActivityImageConfirm : ActivityBaseImageSlider() {

    private var currPic: PicturesRecord? = null
    private var showAll = false
    private val dataManagerAdmin: DataManagerAdmin by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(it)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    override val picturesQuery: SQuery
        get() = if (!showAll) {
            SQuery.newQuery()
                .expr(MxInfoDBContract.Pictures.DELETED, SQuery.Op.NEQ, 1)
                .expr(MxInfoDBContract.Pictures.APPROVED, SQuery.Op.NEQ, -1)
                .expr(MxInfoDBContract.Pictures.APPROVED, SQuery.Op.NEQ, 1)
        } else {
            SQuery.newQuery()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_image_confirm, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_image_confirm).isVisible = false
        val showAllItem = menu.findItem(R.id.menu_image_show_all)
        showAllItem.isChecked = showAll
        showAllItem.title = if (showAllItem.isChecked)
            "all"
        else
            "show confirm"
        thumbsCursor?.let {
            if (it.count > 0 && currPic != null) {
                val confirmMenu = menu.findItem(R.id.menu_image_confirm)
                confirmMenu.isVisible = true
                when (currPic!!.approved) {
                    0L -> confirmMenu.setIcon(R.drawable.actionbar_checkbox_question)
                    -1L -> confirmMenu.setIcon(R.drawable.actionbar_checkbox_empty)
                    1L -> confirmMenu.setIcon(R.drawable.actionbar_checkbox)
                    else -> confirmMenu.icon = null
                }
                title = "RI:" + currPic!!.restId + " A:" + currPic!!.approved + " D:" + currPic!!.deleted
            } else {
                title = "Image confirm?"
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("HardwareIds")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.menu_image_confirm) {
            if (currPic != null) {
                var status = currPic!!.approved.toInt() + 1
                if (status == 2) {
                    status = -1
                }
                val approve = Approved()
                approve.id = currPic!!.restId
                approve.changeuser = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
                approve.status = status
                dataManagerAdmin.approvePicture(approve).subscribe(object : BaseSingleObserver<ApproveResponse>(this) {
                    override fun onSuccess(result: ApproveResponse) {
                        Toast.makeText(this@ActivityImageConfirm, result.message, Toast.LENGTH_LONG).show()
                    }

                })
            } else {
                Toast.makeText(this, "picture not set", Toast.LENGTH_SHORT).show()
            }
        } else if (i == R.id.menu_image_show_all) {
            showAll = !showAll
            restartThumbsLoader()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        super.onLoadFinished(loader, cursor)
        this.title = "Picture confirm " + cursor.count
        currPic?.reload()
        invalidateOptionsMenu()
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        thumbsCursor?.let {
            it.moveToPosition(position)
            currPic = PicturesRecord.fromCursor(it)
        }
        invalidateOptionsMenu()
    }

}
