package info.mx.comlib.retrofit.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RiderTrack {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("urldetailxml")
    @Expose
    private String urldetail;
    @SerializedName("urlmappointjson")
    @Expose
    private String urlmappoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrldetail() {
        return urldetail;
    }

    public void setUrldetail(String urldetail) {
        this.urldetail = urldetail;
    }

    public String getUrlmappoint() {
        return urlmappoint;
    }

    public void setUrlmappoint(String urlmappoint) {
        this.urlmappoint = urlmappoint;
    }
}
