package info.mx.tracks.trackdetail

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import info.mx.tracks.R
import info.mx.core_generated.prefs.MxPreferences
import info.mx.tracks.recyclerview.CursorRecyclerViewAdapter
import info.mx.core_generated.rest.TimeDay
import info.mx.core_generated.rest.TimeHour
import info.mx.core_generated.rest.Weather
import info.mx.core_generated.sqlite.TracksRecord
import info.mx.core_generated.sqlite.WeatherRecord
import info.mx.tracks.util.getDrawableIdentifier
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

/**
 * [Gist](https://gist.github.com/skyfishjy/443b7448f59be978bc59)
 */
class WeatherCursorAdapter(val context: Context, cursor: Cursor?) : CursorRecyclerViewAdapter<WeatherCursorAdapter.ViewHolder>(cursor) {
    private val gson: Gson = Gson()
    private var dayList: ArrayList<Int>? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDate: TextView = view.findViewById(R.id.weather_date)
        val imageWeather: ImageView = view.findViewById(R.id.weather_image)
        val textTemp: TextView = view.findViewById(R.id.weather_temp)
        val textText: TextView = view.findViewById(R.id.weather_text)
        val textAddIt: TextView = view.findViewById(R.id.weather_additional)
        val imageOpen: ImageView = view.findViewById(R.id.weather_status_open)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(viewHolder: ViewHolder, cursor: Cursor?) {
        val recWeather = WeatherRecord.fromCursor(cursor)
        if (recWeather.type == "D") {
            val timeDay = gson.fromJson(recWeather.content, TimeDay::class.java)
            var sdf = SimpleDateFormat("E dd.MM")
            if (!MxPreferences.getInstance().unitsKm) {
                sdf = SimpleDateFormat("E MM.dd")
            }
            viewHolder.textDate.text = sdf.format(Date(timeDay.dt * 1000))
            viewHolder.imageWeather.setImageResource(getWeatherIcon(timeDay.weather))
            if (timeDay.temp != null) {
                viewHolder.textTemp.text = timeDay.temp.day.roundToInt().toString() + "°"
            } else {
                viewHolder.textTemp.text = ""
            }
            if (timeDay.weather != null && timeDay.weather.isNotEmpty()) {
                viewHolder.textText.text = timeDay.weather[0].description
            } else {
                viewHolder.textText.text = ""
            }
            val isOpen = isDayOpen(timeDay.dt)
            viewHolder.imageOpen.visibility = if (isOpen) View.VISIBLE else View.GONE
            viewHolder.textAddIt.visibility = View.GONE
        } else if (recWeather.type == "H") {
            Timber.d("TimeHour:%s", recWeather.content)
            val timeHour = gson.fromJson(recWeather.content, TimeHour::class.java)
            var sdf = SimpleDateFormat("E dd.MM\nHH:mm")
            if (!MxPreferences.getInstance().unitsKm) {
                sdf = SimpleDateFormat("E MM.dd\nhh:mm a")
            }
            viewHolder.textDate.text = sdf.format(Date(timeHour.dt * 1000))
            viewHolder.imageWeather.setImageResource(getWeatherIcon(timeHour.weather))
            if (timeHour.main != null) {
                viewHolder.textTemp.text = timeHour.main.temp.roundToInt().toString() + "°"
            } else {
                viewHolder.textTemp.text = ""
            }
            if (timeHour.weather != null && timeHour.weather.isNotEmpty()) {
                viewHolder.textTemp.text = timeHour.weather[0].description
            } else {
                viewHolder.textTemp.text = ""
            }
            val isOpen = isDayOpen(timeHour.dt)
            viewHolder.imageOpen.visibility =
                if (isOpen) View.VISIBLE else View.GONE
            viewHolder.textAddIt.visibility = View.GONE
        }
    }

    private fun isDayOpen(date: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.time = Date(date * 1000)
        val dow = cal[Calendar.DAY_OF_WEEK]
        return dayList!!.contains(dow)
    }

    fun setTrackClientID(trackClientId: Long) {
        val track = TracksRecord.get(trackClientId)
        dayList = ArrayList()
        if (track == null) {
            return
        }
        if (track.openmondays == 1L) {
            dayList!!.add(Calendar.MONDAY)
        }
        if (track.opentuesdays == 1L) {
            dayList!!.add(Calendar.TUESDAY)
        }
        if (track.openwednesday == 1L) {
            dayList!!.add(Calendar.WEDNESDAY)
        }
        if (track.openthursday == 1L) {
            dayList!!.add(Calendar.THURSDAY)
        }
        if (track.openfriday == 1L) {
            dayList!!.add(Calendar.FRIDAY)
        }
        if (track.opensaturday == 1L) {
            dayList!!.add(Calendar.SATURDAY)
        }
        if (track.opensunday == 1L) {
            dayList!!.add(Calendar.SUNDAY)
        }
    }

    private fun getWeatherIcon(weather: List<Weather?>?): Int {
        var resId = 0
        if (weather == null) {
            return resId
        }
        if (weather.isEmpty()) {
            return resId
        }
        if (weather[0] == null) {
            return resId
        }
        var icon = weather[0]!!.icon
        val id = weather[0]!!.id.toLong()
        icon = icon.replace("ddd".toRegex(), "d").replace("nnn".toRegex(), "n").replace("dd".toRegex(), "d").replace("nn".toRegex(), "n")
        try {
            resId = context.resources.getDrawableIdentifier("weather_$id", context.packageName)
        } catch (_: Exception) {
        }
        try {
            if (resId == 0) {
                resId = context.resources.getDrawableIdentifier("weather_$icon", context.packageName)
            }
        } catch (_: Exception) {
        }
        if (resId == 0) {
            resId = R.drawable.weather_nad
        }
        return resId
    }

}
