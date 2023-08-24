
package info.mx.comlib.retrofit.service.model.google;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Viewport implements Serializable, Parcelable {

    @SerializedName("northeast")
    @Expose
    private Northeast_ northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest_ southwest;
    public final static Creator<Viewport> CREATOR = new Creator<Viewport>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Viewport createFromParcel(Parcel in) {
            return new Viewport(in);
        }

        public Viewport[] newArray(int size) {
            return (new Viewport[size]);
        }

    };
    private final static long serialVersionUID = 2050130155807190656L;

    protected Viewport(Parcel in) {
        this.northeast = ((Northeast_) in.readValue((Northeast_.class.getClassLoader())));
        this.southwest = ((Southwest_) in.readValue((Southwest_.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Viewport() {
    }

    /**
     * @param southwest
     * @param northeast
     */
    public Viewport(Northeast_ northeast, Southwest_ southwest) {
        super();
        this.northeast = northeast;
        this.southwest = southwest;
    }

    public Northeast_ getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast_ northeast) {
        this.northeast = northeast;
    }

    public Southwest_ getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest_ southwest) {
        this.southwest = southwest;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(northeast);
        dest.writeValue(southwest);
    }

    public int describeContents() {
        return 0;
    }

}
