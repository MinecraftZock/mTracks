package info.mx.tracks.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * Custom ViewPager that handles NullPointerException in draw().
 *
 * @deprecated This class is no longer used. All ViewPager usages have been migrated to ViewPager2.
 * This class can be safely removed once confirmed no legacy code depends on it.
 */
@Deprecated(
    message = "Use ViewPager2 instead. All usages have been migrated.",
    replaceWith = ReplaceWith("androidx.viewpager2.widget.ViewPager2")
)
class NoCrashViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        isSaveEnabled = false
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        isSaveEnabled = false
    }

    override fun draw(canvas: Canvas) {
        try {
            super.draw(canvas)
        } catch (_: NullPointerException) {
        }

    }
}
