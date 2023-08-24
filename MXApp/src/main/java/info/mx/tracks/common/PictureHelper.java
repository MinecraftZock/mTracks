package info.mx.tracks.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.robotoworks.mechanoid.ops.Ops;

import java.io.File;

import timber.log.Timber;
import info.mx.tracks.R;
import info.mx.tracks.ops.OpDownLoadImageOperation;
import info.mx.tracks.ops.OpResetLocalImagesOperation;
import info.mx.tracks.prefs.MxPreferences;
import info.mx.tracks.sqlite.PicturesRecord;

public class PictureHelper {

    public static boolean checkAndSetImage(Context context, PicturesRecord record, ImageView imgView, String filepathLocal, int wishSize) {
        boolean res = false;
        boolean thumb = wishSize == Math.round(context.getResources().getDimension(R.dimen.thumbnail_size_dp));
        MxPreferences prefs = MxPreferences.getInstance();
        int size = wishSize;

        // crop image if too big
        if (!thumb && prefs.getImageCropTo() > 0 && size > prefs.getImageCropTo()) {
            size = prefs.getImageCropTo();
        }

        File imgFile = new File(filepathLocal);

        // in case if not valid (=Error) correct it
        if (!imgFile.exists() && !filepathLocal.equals("")) {
            Timber.w("File missing %s", filepathLocal);
            if (thumb) {
                record.setLocalthumb("");
            } else {
                record.setLocalfile("");
            }
            record.save();
            filepathLocal = "";
        }

        if (filepathLocal.equals("")) {
            Timber.d("download " + record.getId() + " " + record.getRestId() + " file:" + filepathLocal);
            Intent intent = OpDownLoadImageOperation.newIntent(record.getId(), size, thumb);
            Ops.execute(intent);
        } else {
            File file2Set = new File(filepathLocal);
            if (!file2Set.exists()) {
                Timber.w("not exists %s", filepathLocal);
            } else {
                //                http://developer.android.com/training/displaying-bitmaps/index.html
                //                if (imgView.getDrawable() != null) {
                //                    Bitmap mBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                //                    if (mBitmap != null && !mBitmap.isRecycled()) {
                //                        mBitmap.recycle();
                //                    }
                //                }
                //                /storage/emulated/0/Android/data/info.hannes.mxadmin/files/id298_480.png
                //                /shell/emulated/0/Android/data/info.hannes.mxadmin/files/id298_480.png <-- real
                Drawable drawable = Drawable.createFromPath(filepathLocal);
                imgView.setImageDrawable(drawable);

                // local images seems to be corrupt
                if (drawable == null) {
                    Ops.execute(OpResetLocalImagesOperation.newIntent());
                }
                Timber.d("Id:" + record.getId() + " size:" + size + " h:" + imgView.getHeight() + " w:" + imgView.getWidth());
                res = true;
            }
        }
        return res;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
