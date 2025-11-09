
package info.mx.comlib.retrofit.service.model.google;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("JavadocDeclaration")
public class Southwest_ implements Serializable, Parcelable {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    public final static Creator<Southwest_> CREATOR = new Creator<>() {

        public Southwest_ createFromParcel(Parcel in) {
            return new Southwest_(in);
        }

        public Southwest_[] newArray(int size) {
            return (new Southwest_[size]);
        }

    };
    private final static long serialVersionUID = -6522291458062169510L;

    protected Southwest_(Parcel in) {
        this.lat = ((Double) in.readValue((Double.class.getClassLoader())));
        this.lng = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Southwest_() {
    }

    /**
     * @param lng
     * @param lat
     */
    public Southwest_(Double lat, Double lng) {
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
