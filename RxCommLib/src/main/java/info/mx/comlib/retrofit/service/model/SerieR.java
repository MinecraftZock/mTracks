package info.mx.comlib.retrofit.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SerieR implements Serializable, Parcelable {

    @SerializedName("changed")
    @Expose
    private Integer changed = 0;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("seriesurl")
    @Expose
    private String seriesurl;
    public final static Parcelable.Creator<SerieR> CREATOR = new Creator<SerieR>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SerieR createFromParcel(Parcel in) {
            SerieR instance = new SerieR();
            instance.changed = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.seriesurl = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public SerieR[] newArray(int size) {
            return (new SerieR[size]);
        }

    };
    private final static long serialVersionUID = 4184335180264082450L;

    public Integer getChanged() {
        return changed;
    }

    public void setChanged(Integer changed) {
        this.changed = changed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeriesurl() {
        return seriesurl;
    }

    public void setSeriesurl(String seriesurl) {
        this.seriesurl = seriesurl;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(changed);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(seriesurl);
    }

    public int describeContents() {
        return 0;
    }

}
