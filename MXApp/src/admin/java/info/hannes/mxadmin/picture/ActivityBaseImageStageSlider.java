package info.hannes.mxadmin.picture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.robotoworks.mechanoid.db.SQuery;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract;
import info.hannes.mechadmin_gen.sqlite.PictureStageRecord;
import timber.log.Timber;
import info.mx.tracks.base.ActivityRx;
import info.mx.tracks.DiskReceiver;
import info.mx.tracks.R;
import info.mx.tracks.sqlite.TracksRecord;
import info.mx.tracks.trackdetail.ImageCursorAdapter;
import info.mx.tracks.util.SystemUiHider;
import info.mx.tracks.views.NoCrashViewPager;

public abstract class ActivityBaseImageStageSlider extends ActivityRx implements LoaderManager.LoaderCallbacks<Cursor>,
        ImageCursorAdapter.OnImageListItemClick, ViewPager.OnPageChangeListener {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 4000;
    private NoCrashViewPager imageViewPager;
    private AdapterImageStagePager imageSwipePagerAdapter;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Timber.d("%s", position);
        thumbsGallery.scrollToPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise, will show the system UI visibility upon interaction.
     */

    protected static final int LOADER_PICTURE_THUMBS = 0;
    public static final String TRACK_ID = "trackID";
    public static final String IMAGE_CLIENT_ID = "imageClientID";

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private long trackId;
    private long imageClientId;
    protected long trackRestId;
    private ImageCursorAdapter thumbsAdapter;

    private RecyclerView thumbsGallery;
    private DiskReceiver diskReceiver;
    protected long currPictureRestId;
    protected Cursor thumbsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_activity_image_slider);

        trackId = this.getIntent().getLongExtra(TRACK_ID, 0);
        imageClientId = this.getIntent().getLongExtra(IMAGE_CLIENT_ID, 0);

        TracksRecord record = TracksRecord.get(trackId);
        if (record != null) {
            trackRestId = record.getRestId();
        }

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        thumbsGallery = findViewById(R.id.gallery_thumbs);

        thumbsAdapter = new ImageCursorAdapter(this, null, Math.round(getResources().getDimension(R.dimen.thumbnail_size_dp)), false);
        LinearLayoutManager layoutRecycler = new LinearLayoutManager(this);
        layoutRecycler.setOrientation(LinearLayoutManager.HORIZONTAL);
        thumbsGallery.setLayoutManager(layoutRecycler);
        thumbsGallery.setAdapter(thumbsAdapter);

        initViewPager();

        // Set up an instance of SystemUiHider to control the system UI for this activity.
        mSystemUiHider = SystemUiHider.Companion.getInstance(this, imageViewPager, SystemUiHider.FLAG_HIDE_NAVIGATION);
        //        mSystemUiHider = SystemUiHider.getInstance(this, imageView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
            // Cached values.
            int mControlsHeight;
            int mShortAnimTime;

            @Override
            public void onVisibilityChange(boolean visible) {
                // If the ViewPropertyAnimator API is available (Honeycomb MR2 and later), use it to animate the
                // in-layout UI controls at the bottom of the screen.
                if (mControlsHeight == 0) {
                    mControlsHeight = controlsView.getHeight();
                }
                if (mShortAnimTime == 0) {
                    mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                }
                controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);

                if (visible /* && AUTO_HIDE */) {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.gallery_thumbs).setOnTouchListener(mDelayHideTouchListener);
    }

    private void initViewPager() {
        imageSwipePagerAdapter = new AdapterImageStagePager(getSupportFragmentManager());
        imageViewPager = findViewById(R.id.fragmentImagePager);
        imageViewPager.setAdapter(imageSwipePagerAdapter);
        imageViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        trackId = savedInstanceState.getLong(TRACK_ID);
        imageClientId = savedInstanceState.getLong(IMAGE_CLIENT_ID);

        TracksRecord record = TracksRecord.get(trackId);
        if (record != null) {
            trackRestId = record.getRestId();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putLong(TRACK_ID, trackId);
        savedInstanceState.putLong(IMAGE_CLIENT_ID, imageClientId);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been created, to briefly hint to the user that UI controls are available.
        delayedHide(2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        diskReceiver = new DiskReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
        registerReceiver(diskReceiver, filter);
        getSupportLoaderManager().initLoader(LOADER_PICTURE_THUMBS, null, this);
    }

    @Override
    public void onPause() {
        if (diskReceiver != null) {
            try {
                unregisterReceiver(diskReceiver);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        getSupportLoaderManager().destroyLoader(LOADER_PICTURE_THUMBS);
        super.onPause();
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };

    Handler mHideHandler = new Handler(Looper.getMainLooper());
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    protected void setImage(PictureStageRecord record) {
        if (thumbsCursor != null) {
            thumbsCursor.moveToFirst();
            thumbsCursor.moveToPrevious();
            int pos = -1;
            while (thumbsCursor.moveToNext()) {
                PictureStageRecord pic = PictureStageRecord.fromCursor(thumbsCursor);
                pos++;
                if (pic.getId() == record.getId()) {
                    thumbsGallery.scrollToPosition(pos);
                    currPictureRestId = record.getRestId();
                    Timber.i("set Img %s", pos);

                    break;
                }
            }
        }
    }

    protected abstract SQuery getPicturesQuery();

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loader, Bundle bundle) {
        switch (loader) {
            case LOADER_PICTURE_THUMBS:
                SQuery query = getPicturesQuery();
                return query.createSupportLoader(MxAdminDBContract.PictureStage.CONTENT_URI, null, MxAdminDBContract.PictureStage.REST_ID);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_PICTURE_THUMBS:
                thumbsCursor = cursor;
                Timber.d("LOADER_PICTURE_THUMBS %s", cursor.getCount());
                int position = -1;
                while (cursor.moveToNext()) {
                    position = cursor.getPosition();
                    PictureStageRecord rec = PictureStageRecord.fromCursor(cursor);
                    if (rec.getRestId() == imageClientId) {
                        setImage(rec);
                        break;
                    }
                }
                thumbsAdapter.swapCursor(cursor);
                if (imageSwipePagerAdapter.setCursor(cursor)) { //prevent flickering
                    imageViewPager.setCurrentItem(position);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_PICTURE_THUMBS:
                thumbsAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onImageItemClick(int position, long imageRestId) {
        this.imageClientId = imageRestId;
        openImageFragment(position);
    }

    private void openImageFragment(int position) {
        if (imageViewPager.getAdapter() != null) {
            ((AdapterImageStagePager) imageViewPager.getAdapter()).resetZoom();
        }
        imageViewPager.setCurrentItem(position, true);
    }

    protected void restartThumbsLoader() {
        getSupportLoaderManager().restartLoader(LOADER_PICTURE_THUMBS, null, this);
    }
}
