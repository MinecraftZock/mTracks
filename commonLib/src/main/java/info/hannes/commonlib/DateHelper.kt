package info.hannes.commonlib

import java.text.DateFormatSymbols
import java.util.Locale
import kotlin.math.floor
import kotlin.math.roundToInt

object DateHelper {
    val shortWeekdays: Array<String>
        get() {
            val shortWeekdays: Array<String> = DateFormatSymbols().shortWeekdays
            for (i in shortWeekdays.indices) {
                if (shortWeekdays[i].length > 2) {
                    shortWeekdays[i] = shortWeekdays[i].substring(0, 2)
                }
            }
            return shortWeekdays
        }

    fun getTimeStrFromMinutes(minutes: Int): String {
        val hour = floor((minutes / 60).toDouble()).toInt()
        val min = (minutes - hour * 60).toFloat().roundToInt()
        return hour.toString() + ":" + String.format(Locale.getDefault(), "%02d", min) + " h"
    }

    fun getTimeStrFromMinutesDay(minutes: Int): String {
        val day = floor((minutes / 60 / 24).toDouble()).toInt()
        val hour = floor((minutes / 60).toDouble()).toInt() - day * 24
        val min = (minutes - hour * 60 - day * 24 * 60).toFloat().roundToInt()
        val prefix = if (day > 0) {
            String.format(Locale.getDefault(), "%d", day) + "d"
        } else {
            ""
        }
        return prefix + String.format(Locale.getDefault(), "%02d", hour) + ":" +
                String.format(Locale.getDefault(), "%02d", min)
    }
}
