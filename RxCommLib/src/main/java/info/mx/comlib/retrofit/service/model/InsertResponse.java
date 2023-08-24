package info.mx.comlib.retrofit.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsertResponse {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("trackRestId")
    @Expose
    private int trackRestId;
    @SerializedName("changed")
    @Expose
    private long changed;
    @SerializedName("message")
    @Expose
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getChanged() {
        return changed;
    }

    public void setChanged(long changed) {
        this.changed = changed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the trackRestId
     */
    public int getTrackRestId() {
        return trackRestId;
    }

    /**
     * @param trackRestId the trackRestId to set
     */
    public void setTrackRestId(int trackRestId) {
        this.trackRestId = trackRestId;
    }

}
