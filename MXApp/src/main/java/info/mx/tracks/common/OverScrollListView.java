package info.mx.tracks.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import timber.log.Timber;

public class OverScrollListView extends ListView {

    public interface OverScrollListener {
        void onOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                            int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);

        void onOverScroll(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }

    private OverScrollListener mOverScrollListener = null;

    public OverScrollListView(Context context) {
        super(context);
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        init();
    }

    public OverScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        setScrollbarFadingEnabled(false);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        Timber.d("scrollX:" + scrollX + " scrollY:" + scrollY + " deltaX:" + deltaX + " deltaY:" + deltaY + " scrollRangeX:" + scrollRangeX + " " +
                "scrollRangeY:" + scrollRangeY + " maxOverScrollX:" + maxOverScrollX + " maxOverScrollY:" + maxOverScrollX);
        if (null != mOverScrollListener) {
            mOverScrollListener.onOverScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                    maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0, 200, isTouchEvent);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Timber.d("scrollX:" + scrollX + " scrollY:" + scrollY + " clampedX:" + clampedX + " clampedY:" + clampedX);
        if (null != mOverScrollListener) {
            mOverScrollListener.onOverScroll(scrollX, scrollY, clampedX, clampedY);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void setOnOverScrollListener(OverScrollListener listener) {
        mOverScrollListener = listener;
    }
}

