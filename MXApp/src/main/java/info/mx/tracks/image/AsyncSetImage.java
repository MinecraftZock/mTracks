package info.mx.tracks.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

import timber.log.Timber;

public class AsyncSetImage extends AsyncTask<String, Integer, Boolean> {
    private final ImageView imageView;
    private Bitmap bitmap = null;

    public AsyncSetImage(ImageView imageView) {
        this.imageView = imageView;
        imageView.setTag(true);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return true;
        } catch (IOException e) {
            Timber.e(e);
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
