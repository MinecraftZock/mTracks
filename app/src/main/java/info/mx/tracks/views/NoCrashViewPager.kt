package info.mx.tracks.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

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
