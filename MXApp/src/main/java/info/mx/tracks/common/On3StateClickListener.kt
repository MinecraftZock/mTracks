package info.mx.tracks.common

import android.view.View
import android.widget.ImageView

open class On3StateClickListener : View.OnClickListener {
    protected var level = 0
    override fun onClick(view: View) {
        level = (view as ImageView).drawable.level
        level++
        if (level == 3) {
            level = 0
        }
        view.setImageLevel(level)
    }
}
