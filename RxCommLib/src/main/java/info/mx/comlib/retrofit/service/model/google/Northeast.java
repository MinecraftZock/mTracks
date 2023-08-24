
package info.mx.comlib.retrofit.service.model.google;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Northeast implements Serializable, Parcelable {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    public final static Creator<Northeast> CREATOR = new Creator<Northeast>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Northeast createFromParcel(Parcel in) {
            return new Northeast(in);
        }

        public Northeast[] newArray(int size) {
            return (new Northeast[size]);
        }

    };
    private final static long serialVersionUID = -6743818751999386945L;

    protected Northeast(Parcel in) {
        this.lat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lng = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Northeast() {
    }

    /**
     * @param lng
     * @param lat
     */
    public Northeast(Double lat, Double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
    }

    public int describeContents() {
        return 0;
    }

}
