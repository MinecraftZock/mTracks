
package info.mx.core.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class List implements Serializable, Parcelable {

    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("temp")
    @Expose
    private Temp temp;
    @SerializedName("pressure")
    @Expose
    private Double pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("weather")
    @Expose
    private java.util.List<Weather> weather = null;
    @SerializedName("speed")
    @Expose
    private Double speed;
    @SerializedName("deg")
    @Expose
    private Integer deg;
    @SerializedName("clouds")
    @Expose
    private Integer clouds;
    @SerializedName("snow")
    @Expose
    private Double snow;
    @SerializedName("rain")
    @Expose
    private Double rain;
    public final static Parcelable.Creator<List> CREATOR = new Creator<List>() {


        @SuppressWarnings({
                "unchecked"
        })
        public List createFromParcel(Parcel in) {
            List instance = new List();
            instance.dt = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.temp = ((Temp) in.readValue((Temp.class.getClassLoader())));
            instance.pressure = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.humidity = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.weather, (info.mx.core.rest.model.Weather.class.getClassLoader()));
            instance.speed = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.deg = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.clouds = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.snow = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.rain = ((Double) in.readValue((Double.class.getClassLoader())));
            return instance;
        }

        public List[] newArray(int size) {
            return (new List[size]);
        }

    };
    private final static long serialVersionUID = 2901418664714745610L;

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public java.util.List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(java.util.List<Weather> weather) {
        this.weather = weather;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public void setDeg(Integer deg) {
        this.deg = deg;
    }

    public Integer getClouds() {
        return clouds;
    }

    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    public Double getSnow() {
        return snow;
    }

    public void setSnow(Double snow) {
        this.snow = snow;
    }

    public Double getRain() {
        return rain;
    }

    public void setRain(Double rain) {
        this.rain = rain;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dt);
        dest.writeValue(temp);
        dest.writeValue(pressure);
        dest.writeValue(humidity);
        dest.writeList(weather);
        dest.writeValue(speed);
        dest.writeValue(deg);
        dest.writeValue(clouds);
        dest.writeValue(snow);
        dest.writeValue(rain);
    }

    public int describeContents() {
        return 0;
    }

}
