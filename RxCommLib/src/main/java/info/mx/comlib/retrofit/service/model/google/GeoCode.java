
package info.mx.comlib.retrofit.service.model.google;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GeoCode implements Serializable, Parcelable {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;
    public final static Creator<GeoCode> CREATOR = new Creator<GeoCode>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GeoCode createFromParcel(Parcel in) {
            return new GeoCode(in);
        }

        public GeoCode[] newArray(int size) {
            return (new GeoCode[size]);
        }

    };
    private final static long serialVersionUID = 3171710963124198211L;

    protected GeoCode(Parcel in) {
        in.readList(this.results, (Result.class.getClassLoader()));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public GeoCode() {
    }

    /**
     * @param results
     * @param status
     */
    public GeoCode(List<Result> results, String status) {
        super();
        this.results = results;
        this.status = status;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(results);
        dest.writeValue(status);
    }

    public int describeContents() {
        return 0;
    }

}
