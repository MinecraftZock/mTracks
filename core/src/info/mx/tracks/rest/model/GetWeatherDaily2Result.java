
package info.mx.tracks.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetWeatherDaily2Result implements Serializable, Parcelable {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("city")
    @Expose
    private City city;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<info.mx.tracks.rest.model.List> list = null;
    public final static Parcelable.Creator<GetWeatherDaily2Result> CREATOR = new Creator<>() {


        public GetWeatherDaily2Result createFromParcel(Parcel in) {
            GetWeatherDaily2Result instance = new GetWeatherDaily2Result();
            instance.cod = ((String) in.readValue((String.class.getClassLoader())));
            instance.message = ((String) in.readValue((String.class.getClassLoader())));
            instance.city = ((City) in.readValue((City.class.getClassLoader())));
            instance.cnt = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.list = new java.util.ArrayList<>();
            in.readParcelableList(instance.list, info.mx.tracks.rest.model.List.class.getClassLoader(), info.mx.tracks.rest.model.List.class);
            return instance;
        }

        public GetWeatherDaily2Result[] newArray(int size) {
            return (new GetWeatherDaily2Result[size]);
        }

    };
    private final static long serialVersionUID = -1281715064981046507L;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public java.util.List<info.mx.tracks.rest.model.List> getList() {
        return list;
    }

    public void setList(java.util.List<info.mx.tracks.rest.model.List> list) {
        this.list = list;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cod);
        dest.writeValue(message);
        dest.writeValue(city);
        dest.writeValue(cnt);
        dest.writeParcelableList(list, flags);
    }

    public int describeContents() {
        return 0;
    }

}
