package info.mx.tracks.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import java.util.Locale;

import info.mx.tracks.BuildConfig;
import info.mx.tracks.MxCoreApplication;
import info.mx.tracks.R;
import info.mx.tracks.prefs.MxPreferences;
import info.mx.tracks.sqlite.MxInfoDBContract.Ratings;
import info.mx.tracks.sqlite.RatingsRecord;
import info.mx.tracks.sqlite.TracksRecord;

public class CommentHelper {

    private static RatingsRecord ratingsRecord;
    private static EditText edtComment;
    private static EditText userName;
    private static RatingBar ratingBar;

    public static void doAddRating(Activity activity, final TracksRecord trackRec) {
        // on add eventId is not used
        doEditRating(activity, 0, trackRec);
    }

    private static void doEditRating(final Activity activity, final int ratingId, TracksRecord trackRec) {
        ratingsRecord = RatingsRecord.get(ratingId);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.newRating));

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        builder.setPositiveButton(R.string.done, (dialog, which) -> {

            final boolean alreadyExist = SQuery.newQuery()
                    .expr(Ratings.TRACK_REST_ID, Op.EQ, trackRec.getRestId())
                    .expr(Ratings.ANDROIDID, Op.EQ, getAndroidID(activity))
                    .expr(Ratings.NOTE, Op.EQ, edtComment.getText().toString()).exists(Ratings.CONTENT_URI);

            if (!alreadyExist) {
                if (ratingsRecord == null) {
                    ratingsRecord = new RatingsRecord();
                }

                ratingsRecord.setTrackRestId(trackRec.getRestId());
                ratingsRecord.setApproved(0);
                ratingsRecord.setRating((long) ratingBar.getRating());
                ratingsRecord.setUsername(userName.getText().toString());
                ratingsRecord.setNote(edtComment.getText().toString());
                ratingsRecord.setCountry(Locale.getDefault().getCountry());
                ratingsRecord.setChanged(System.currentTimeMillis());
                ratingsRecord.setAndroidid(getAndroidID(activity));
                ratingsRecord.save();
                MxCoreApplication.Companion.doSync(true, true, BuildConfig.FLAVOR);
                if (!ratingsRecord.getUsername().equals(activity.getString(R.string.noname))) {
                    final MxPreferences prefs1 = MxPreferences.getInstance();
                    prefs1.edit().putUsername(ratingsRecord.getUsername().trim()).commit();
                }
            } else {
                Toast.makeText(activity, activity.getString(R.string.comment_alredy_exists), Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dlg = builder.create();
        LayoutInflater inflater = dlg.getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.layout_comment_add, null);
        userName = view.findViewById(R.id.com_username);
        edtComment = view.findViewById(R.id.com_note);
        ratingBar = view.findViewById(R.id.com_rating);
        if (ratingsRecord != null) {
            edtComment.setText(ratingsRecord.getNote());
        }

        final MxPreferences prefs = MxPreferences.getInstance();
        userName.setHint(R.string.default_username);
        userName.setText(prefs.getUsername());
        dlg.setView(view);
        dlg.show();
    }

    private static String getAndroidID(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void doDeleteNote(final Context context, final int ratingId) {
        final RatingsRecord record = RatingsRecord.get(ratingId);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (record != null) {
                record.delete();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });
        final AlertDialog delDialog = builder.create();
        delDialog.setMessage("Do you really want to delete this Note?"); // TODO
        delDialog.show();
    }

}
