package info.mx.tracks.common

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import timber.log.Timber

class OverScrollListView : ListView {

    interface OverScrollListener {
        fun onOverScrollBy(
            deltaX: Int,
            deltaY: Int,
            scrollX: Int,
            scrollY: Int,
            scrollRangeX: Int,
            scrollRangeY: Int,
            maxOverScrollX: Int,
            maxOverScrollY: Int,
            isTouchEvent: Boolean
        )

        fun onOverScroll(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean)
    }

    private var mOverScrollListener: OverScrollListener? = null

    constructor(context: Context?) : super(context) {
        overScrollMode = OVER_SCROLL_ALWAYS
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        overScrollMode = OVER_SCROLL_ALWAYS
        isScrollbarFadingEnabled = false
    }

    override fun overScrollBy(
        deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int,
        maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean
    ): Boolean {
        Timber.d(
            "scrollX:" + scrollX + " scrollY:" + scrollY + " deltaX:" + deltaX + " deltaY:" + deltaY + " scrollRangeX:" + scrollRangeX + " " +
                    "scrollRangeY:" + scrollRangeY + " maxOverScrollX:" + maxOverScrollX + " maxOverScrollY:" + maxOverScrollX
        )
        if (null != mOverScrollListener) {
            mOverScrollListener!!.onOverScrollBy(
                deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
                maxOverScrollX, maxOverScrollY, isTouchEvent
            )
        }
        return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0, 200, isTouchEvent)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        Timber.d("scrollX:$scrollX scrollY:$scrollY clampedX:$clampedX clampedY:$clampedX")
        if (null != mOverScrollListener) {
            mOverScrollListener!!.onOverScroll(scrollX, scrollY, clampedX, clampedY)
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }

    fun setOnOverScrollListener(listener: OverScrollListener?) {
        mOverScrollListener = listener
    }
}
