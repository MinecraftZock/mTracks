package info.mx.core.rest.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WeatherList : Serializable, Parcelable {
    @SerializedName("dt")
    @Expose
    var dt: Int? = null

    @SerializedName("temp")
    @Expose
    private var temp: Temp? = null

    @SerializedName("pressure")
    @Expose
    private var pressure: Double? = null

    @SerializedName("humidity")
    @Expose
    private var humidity: Int? = null

    @SerializedName("weather")
    @Expose
    private var weather: MutableList<Weather>? = null

    @SerializedName("speed")
    @Expose
    private var speed: Double? = null

    @SerializedName("deg")
    @Expose
    private var deg: Int? = null

    @SerializedName("clouds")
    @Expose
    private var clouds: Int? = null

    @SerializedName("snow")
    @Expose
    private var snow: Double? = null

    @SerializedName("rain")
    @Expose
    private var rain: Double? = null

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(dt)
        dest.writeValue(temp)
        dest.writeValue(pressure)
        dest.writeValue(humidity)
        dest.writeList(weather)
        dest.writeValue(speed)
        dest.writeValue(deg)
        dest.writeValue(clouds)
        dest.writeValue(snow)
        dest.writeValue(rain)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WeatherList?> = object : Parcelable.Creator<WeatherList?> {
            override fun createFromParcel(`in`: Parcel): WeatherList {
                val instance = WeatherList()
                instance.dt = (`in`.readValue((Int::class.java.classLoader)) as Int?)
                instance.temp = (`in`.readValue((Temp::class.java.classLoader)) as Temp?)
                instance.pressure = (`in`.readValue((Double::class.java.classLoader)) as Double?)
                instance.humidity = (`in`.readValue((Int::class.java.classLoader)) as Int?)
                @Suppress("UNCHECKED_CAST")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    @Suppress("UNCHECKED_CAST")
                    instance.weather = `in`.readArrayList(Weather::class.java.classLoader, Weather::class.java) as? MutableList<Weather>
                } else {
                    @Suppress("UNCHECKED_CAST", "DEPRECATION")
                    instance.weather = `in`.readArrayList(Weather::class.java.classLoader) as MutableList<Weather>?
                }
                instance.speed = (`in`.readValue((Double::class.java.classLoader)) as Double?)
                instance.deg = (`in`.readValue((Int::class.java.classLoader)) as Int?)
                instance.clouds = (`in`.readValue((Int::class.java.classLoader)) as Int?)
                instance.snow = (`in`.readValue((Double::class.java.classLoader)) as Double?)
                instance.rain = (`in`.readValue((Double::class.java.classLoader)) as Double?)
                return instance
            }

            override fun newArray(size: Int): Array<WeatherList?> {
                return arrayOfNulls(size)
            }
        }
    }
}
