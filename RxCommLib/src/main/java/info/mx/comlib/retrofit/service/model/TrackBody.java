package info.mx.comlib.retrofit.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackBody {

    @SerializedName("changed")
    @Expose
    private Integer changed;
    @SerializedName("androidid")
    @Expose
    private String androidid;
    @SerializedName("appversion")
    @Expose
    private Integer appversion;
    @SerializedName("androidversion")
    @Expose
    private Integer androidversion;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("flavor")
    @Expose
    private String flavor;
    @SerializedName("ip")
    @Expose
    private String ip;

    public Integer getChanged() {
        return changed;
    }

    public void setChanged(Integer changed) {
        this.changed = changed;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public Integer getAppversion() {
        return appversion;
    }

    public void setAppversion(Integer appversion) {
        this.appversion = appversion;
    }

    public Integer getAndroidversion() {
        return androidversion;
    }

    public void setAndroidversion(Integer androidversion) {
        this.androidversion = androidversion;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
