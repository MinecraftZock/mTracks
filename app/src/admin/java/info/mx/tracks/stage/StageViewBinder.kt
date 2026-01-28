package info.mx.tracks.stage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.robotoworks.mechanoid.db.SQuery
import com.robotoworks.mechanoid.ops.Ops
import info.hannes.commonlib.LocationHelper.getFormatDistance
import info.hannes.commonlib.utils.ViewUtils.disableView
import info.hannes.commonlib.utils.ViewUtils.enableView
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.tracks.BuildConfig
import info.mx.tracks.R
import info.mx.tracks.base.FragmentRx
import info.mx.tracks.common.getStageValues
import info.mx.tracks.common.getStringVal
import info.mx.core_generated.ops.AbstractOpSyncFromServerOperation
import info.mx.core_generated.sqlite.MxInfoDBContract.UserActivity
import info.mx.core_generated.sqlite.TrackstageRecord
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StageViewBinder(
    private val fragmentRx: FragmentRx,
    private val activity: Activity,
    private val dataManagerAdmin: DataManagerAdmin
) : SimpleCursorAdapter.ViewBinder {
    @SuppressLint("SetTextI18n")
    @Suppress("deprecation")
    override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
        var res = false
        if (cursor.isClosed) {
            return true
        }
        when (view.id) {
            R.id.textLeft -> {
                val txt: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(cursor.getStageValues(), Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(cursor.getStringVal())
                }
                (view as TextView).text = txt
                view.setOnLongClickListener { view1: View ->
                    val clipboard =
                        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Stage", (view1 as TextView).text.toString())
                    clipboard.setPrimaryClip(clip)
                    false
                }
                res = true
            }

            R.id.textOpenVal -> {
                var anz = 0
                if (cursor.getString(columnIndex) != null) {
                    anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, SQuery.Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, SQuery.Op.EQ, 0)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT)
                }
                (view as TextView).text = if (anz == 0) "" else "" + anz
                res = true
            }

            R.id.textDeclineVal -> {
                var anz = 0
                if (cursor.getString(columnIndex) != null) {
                    anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, SQuery.Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, SQuery.Op.EQ, -1)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT)
                }
                (view as TextView).text = "" + anz
                res = true
            }

            R.id.textAcceptVal -> {
                var anz = 0
                if (cursor.getString(columnIndex) != null) {
                    anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, SQuery.Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, SQuery.Op.EQ, 1)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT)
                }
                (view as TextView).text = " $$anz"
                res = true
            }

            R.id.textDist -> {
                (view as TextView).text = getFormatDistance(true, cursor.getInt(columnIndex))
                res = true
            }

            R.id.btnAccept -> {
                val rec = TrackstageRecord.fromCursor(cursor)
                if (rec.approved == 0L) {
                    enableView(view)
                } else {
                    disableView(view)
                }
                view.tag = cursor.getLong(columnIndex)
                view.setOnClickListener { v: View ->
                    val id = v.tag as Long
                    fragmentRx.addDisposable(
                        dataManagerAdmin.trackStageAccept(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally { Timber.d("()") }
                            .subscribe({
                                rec.delete()
                                val intentM: Intent = AbstractOpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR)
                                Ops.execute(intentM)
                            }
                            ) { e: Throwable? -> Timber.d(e) }
                    )
                }
                res = true
            }

            R.id.btnDecline -> {
                val rec = TrackstageRecord.fromCursor(cursor)
                if (rec.approved == 0L) {
                    enableView(view)
                } else {
                    disableView(view)
                }
                view.tag = cursor.getLong(columnIndex)
                view.setOnClickListener {
                    fragmentRx.registerForContextMenu(view)
                    activity.openContextMenu(view)
                }
                res = true
            }
        }
        return res
    }
}
