package info.mx.tracks.map

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.google.android.gms.maps.SupportMapFragment

//enables the scrolling in a Scrollview
class FragmentMapScroll : SupportMapFragment() {
    private var listener: OnTouchListener? = null

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, savedInstance: Bundle?): View {
        val layout = super.onCreateView(layoutInflater, viewGroup, savedInstance)

        val frameLayout = context?.let { TouchableWrapper(it) }

        frameLayout?.setBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

        (layout as ViewGroup).addView(frameLayout,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        return layout
    }

    fun setListener(onTouchListener: OnTouchListener) {
        listener = onTouchListener
    }

    interface OnTouchListener {
        fun onTouch()
    }

    inner class TouchableWrapper(context: Context) : FrameLayout(context) {

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            if (listener != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> listener!!.onTouch()
                    MotionEvent.ACTION_UP -> listener!!.onTouch()
                }
            }
            return super.dispatchTouchEvent(event)
        }
    }
}
