package info.mx.tracks.stage;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import androidx.cursoradapter.widget.SimpleCursorAdapter.ViewBinder;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;
import com.robotoworks.mechanoid.ops.Ops;

import info.hannes.commonlib.LocationHelper;
import info.hannes.commonlib.utils.ViewUtils;
import info.hannes.mxadmin.service.DataManagerAdmin;
import info.mx.tracks.base.FragmentRx;
import info.mx.tracks.BuildConfig;
import info.mx.tracks.R;
import info.mx.tracks.common.StageHelperExtension;
import info.mx.tracks.ops.OpSyncFromServerOperation;
import info.mx.tracks.sqlite.MxInfoDBContract.UserActivity;
import info.mx.tracks.sqlite.TrackstageRecord;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class StageViewBinder implements ViewBinder {

    private final FragmentRx fragmentRx;
    private final Activity activity;
    private DataManagerAdmin dataManagerAdmin;

    public StageViewBinder(FragmentRx fragmentRx, Activity activity, DataManagerAdmin dataManagerAdmin) {
        this.fragmentRx = fragmentRx;
        this.activity = activity;
        this.dataManagerAdmin = dataManagerAdmin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean setViewValue(final View view, Cursor cursor, int columnIndex) {
        boolean res = false;
        if (cursor.isClosed()) {
            return true;
        }
        if (view.getId() == R.id.textLeft) {
            final Spanned txt;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                txt = Html.fromHtml(StageHelperExtension.getStageValues(cursor), Html.FROM_HTML_MODE_LEGACY);
            } else {
                txt = Html.fromHtml(StageHelperExtension.getStringVal(cursor));
            }
            ((TextView) view).setText(txt);
            view.setOnLongClickListener(view1 -> {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Stage", ((TextView) view1).getText().toString());
                clipboard.setPrimaryClip(clip);
                return false;
            });
            res = true;
        } else if (view.getId() == R.id.textOpenVal) {
            int anz = 0;
            if (cursor.getString(columnIndex) != null) {
                anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, Op.EQ, 0)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT);
            }
            ((TextView) view).setText(anz == 0 ? "" : "" + anz);
            res = true;
        } else if (view.getId() == R.id.textDeclineVal) {
            int anz = 0;
            if (cursor.getString(columnIndex) != null) {
                anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, Op.EQ, -1)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT);
            }
            ((TextView) view).setText("" + anz);
            res = true;
        } else if (view.getId() == R.id.textAcceptVal) {
            int anz = 0;
            if (cursor.getString(columnIndex) != null) {
                anz = SQuery.newQuery()
                        .expr(UserActivity.ANDROIDID, Op.EQ, cursor.getString(columnIndex))
                        .expr(UserActivity.APPROVED, Op.EQ, 1)
                        .firstInt(UserActivity.CONTENT_URI, UserActivity.CNT);
            }
            ((TextView) view).setText(" $" + anz);
            res = true;
        } else if (view.getId() == R.id.textDist) {
            ((TextView) view).setText(LocationHelper.INSTANCE.getFormatDistance(true, cursor.getInt(columnIndex)));
            res = true;
        } else if (view.getId() == R.id.btnAccept) {
            final TrackstageRecord rec = TrackstageRecord.fromCursor(cursor);
            if (rec.getApproved() == 0) {
                ViewUtils.enableView(view);
            } else {
                ViewUtils.INSTANCE.disableView(view);
            }
            view.setTag(cursor.getLong(columnIndex));
            view.setOnClickListener(v -> {
                final long id = (Long) v.getTag();
                fragmentRx.addDisposable(dataManagerAdmin.trackStageAccept(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> Timber.d("()"))
                        .subscribe(result -> {
                                    rec.delete();
                                    final Intent intentM = OpSyncFromServerOperation.newIntent(true, BuildConfig.FLAVOR);
                                    Ops.execute(intentM);
                                }, e -> Timber.d("()")
                        )
                );
            });
            res = true;
        } else if (view.getId() == R.id.btnDecline) {
            final TrackstageRecord rec = TrackstageRecord.fromCursor(cursor);
            if (rec.getApproved() == 0) {
                ViewUtils.enableView(view);
            } else {
                ViewUtils.INSTANCE.disableView(view);
            }
            view.setTag(cursor.getLong(columnIndex));
            view.setOnClickListener(v -> {
                fragmentRx.registerForContextMenu(view);
                activity.openContextMenu(view);
            });

            res = true;
        }
        return res;
    }

}
