package info.mx.core.rest.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetWeatherDaily2Result : Serializable, Parcelable {
    @SerializedName("cod")
    @Expose
    private var cod: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("city")
    @Expose
    private var city: City? = null

    @SerializedName("cnt")
    @Expose
    private var cnt: Int? = null

    @SerializedName("list")
    @Expose
    var weatherList: MutableList<WeatherList> = mutableListOf()

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(cod)
        dest.writeValue(message)
        dest.writeValue(city)
        dest.writeValue(cnt)
        dest.writeList(weatherList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GetWeatherDaily2Result?> =
            object : Parcelable.Creator<GetWeatherDaily2Result?> {
                override fun createFromParcel(`in`: Parcel): GetWeatherDaily2Result {
                    val instance = GetWeatherDaily2Result()
                    instance.cod = (`in`.readValue((String::class.java.classLoader)) as String?)
                    instance.message = (`in`.readValue((String::class.java.classLoader)) as String?)
                    instance.city = (`in`.readValue((City::class.java.classLoader)) as City?)
                    instance.cnt = (`in`.readValue((Int::class.java.classLoader)) as Int?)
                    instance.weatherList = mutableListOf()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        `in`.readParcelableList<WeatherList>(
                            instance.weatherList,
                            WeatherList::class.java.classLoader,
                            WeatherList::class.java
                        )
                    } else {
                        @Suppress("UNCHECKED_CAST", "DEPRECATION")
                        instance.weatherList = `in`.readArrayList(WeatherList::class.java.classLoader) as? MutableList<WeatherList> ?: mutableListOf()

                    }
                    return instance
                }

                override fun newArray(size: Int): Array<GetWeatherDaily2Result?> {
                    return (arrayOfNulls(size))
                }
            }
    }
}
