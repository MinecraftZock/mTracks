package info.mx.tracks.common

import android.content.Context
import info.mx.tracks.R

object StatusHelper {
    fun getLongStatusText(context: Context, trackStatus: String?): CharSequence {
        return when (trackStatus) {
            null -> ""
            "O" -> context.getText(R.string.open)
            "CS" -> context.getText(R.string.closed_season)
            "NY" -> context.getText(R.string.closed_notyet)
            "C" -> context.getText(R.string.closed)
            "CT" -> context.getText(R.string.closed_temp)
            else -> ""
        }
    }

    fun getShortStatusText(context: Context, trackStatus: String?): CharSequence {
        return if (trackStatus == null) {
            ""
        } else if (trackStatus.contentEquals(context.getText(R.string.open))) {
            "O"
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_season))) {
            "CS"
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_notyet))) {
            "NY"
        } else if (trackStatus.contentEquals(context.getText(R.string.closed))) {
            "C"
        } else if (trackStatus.contentEquals(context.getText(R.string.closed_temp))) {
            "CT"
        } else {
            ""
        }
    }
}
