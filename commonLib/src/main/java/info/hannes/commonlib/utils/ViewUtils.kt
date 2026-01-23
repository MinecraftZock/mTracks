package info.hannes.commonlib.utils

import android.view.View
import android.view.View.MeasureSpec
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView

object ViewUtils {

    @JvmStatic
    fun enableView(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.isEnabled = true
                view.alpha = 1f
            }
        }
    }

    fun disableView(vararg views: View?) {
        disableView(0.5f, *views)
    }

    private fun disableView(alpha: Float, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.isEnabled = false
                view.alpha = alpha
            }
        }
    }

    fun disableButtons(vararg buttons: Button?) {
        for (button in buttons) {
            if (button != null) {
                button.isEnabled = false
                button.background.alpha = 120
            }
        }
    }

    fun disableButtons(vararg buttons: ImageView?) {
        for (button in buttons) {
            if (button != null) {
                button.isEnabled = false
                button.background.alpha = 120
            }
        }
    }

    fun enableButtons(vararg buttons: ImageView?) {
        for (button in buttons) {
            if (button != null) {
                button.isEnabled = true
                button.background.alpha = 255
            }
        }
    }

    @JvmStatic
    fun setTotalHeightOfListView(listView: ListView) {
        val mAdapter = listView.adapter
        var totalHeight = 0
        for (i in 0 until mAdapter.count) {
            val mView = mAdapter.getView(i, null, listView)
            mView.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            totalHeight += mView.measuredHeight
            totalHeight += listView.dividerHeight
        }

        // missing listView.getPaddingTop() and bottom
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (mAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }
}
