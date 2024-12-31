package info.mx.tracks.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import info.mx.tracks.R;
import info.mx.tracks.common.PictureHelper;

public class AdapterImageUrisAdapter extends BaseAdapter {

    private final ArrayList<Uri> uris;
    private final Context context;
    private static final int PERCENTAGE = 30;
    private static int desiredScreenWidth;
    private static int desiredScreenHeight;

    public AdapterImageUrisAdapter(Context context, ArrayList<Uri> uris) {
        this.uris = uris;
        this.context = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        desiredScreenWidth = displaymetrics.widthPixels * 80 / 100;
        desiredScreenHeight = displaymetrics.heightPixels * PERCENTAGE / 100;
    }

    public static int getDesiredScreenWidth() {
        return desiredScreenWidth;
    }


    public static int getDesiredScreenHeight() {
        return desiredScreenHeight;
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public Object getItem(int position) {
        return uris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_image, parent, false);
        }

        View layout = view.findViewById(R.id.layoutPreview);
        ImageView imageView = view.findViewById(R.id.imageShare);
        if (getItem(position).toString().startsWith("http")) {
            if (imageView.getTag() == null) {
                AsyncSetImage setImageTask = new AsyncSetImage(imageView);
                setImageTask.execute(getItem(position).toString());
            }
        } else if (getItem(position).toString().startsWith("content")) {
            String realPath = PictureHelper.getRealPathFromUri(context, Uri.parse(getItem(position).toString()));
            if (realPath != null) {
                File imgFile = new File(realPath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            } else {
                Uri uri = Uri.parse(getItem(position).toString());
                try {
                    Bitmap myBitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
                    imageView.setImageBitmap(myBitmap);
                } catch (FileNotFoundException ignored) {
                }
            }
        } else if (getItem(position).toString().startsWith("file:///")) {
            String realPath = ((Uri) getItem(position)).getPath();
            File imgFile = new File(realPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }

        // ListView uses all place, grid only halve of it
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = getDesiredScreenWidth() / (getCount() == 1 ? 1 : 2);
        params.height = params.width * 3 / 4;
        layout.setLayoutParams(params);
        return view;
    }
}
