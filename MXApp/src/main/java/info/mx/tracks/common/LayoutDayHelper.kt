package info.mx.tracks.common

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import info.mx.tracks.R

fun View.setDayLayout(value: Boolean) {
    TextViewCompat.setTextAppearance(this as TextView, R.style.textAppearanceSmall)
    if (!value) {
        this.background = ContextCompat.getDrawable(this.context, R.drawable.rounded_edges_grey)
        this.setTextColor(Color.BLACK)
    } else {
        this.background = ContextCompat.getDrawable(this.context, R.drawable.rounded_edges_blue)
        this.setTextColor(Color.WHITE)
    }
}
