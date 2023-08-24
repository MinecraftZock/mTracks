package info.mx.comlib.retrofit.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PictureR implements Serializable, Parcelable {

    @SerializedName("androidid")
    @Expose
    private String androidid;
    @SerializedName("approved")
    @Expose
    private Integer approved = 0;
    @SerializedName("changed")
    @Expose
    private Integer changed = 0;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("trackId")
    @Expose
    private Integer trackId = 0;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("deleted")
    @Expose
    private Integer deleted = 0;
    public final static Parcelable.Creator<PictureR> CREATOR = new Creator<PictureR>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PictureR createFromParcel(Parcel in) {
            PictureR instance = new PictureR();
            instance.androidid = ((String) in.readValue((String.class.getClassLoader())));
            instance.approved = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.changed = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.deleted = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.comment = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.trackId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.username = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PictureR[] newArray(int size) {
            return (new PictureR[size]);
        }

    };
    private final static long serialVersionUID = 8688911732956334948L;

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public Integer getChanged() {
        return changed;
    }

    public void setChanged(Integer changed) {
        this.changed = changed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(androidid);
        dest.writeValue(approved);
        dest.writeValue(changed);
        dest.writeValue(deleted);
        dest.writeValue(comment);
        dest.writeValue(id);
        dest.writeValue(trackId);
        dest.writeValue(username);
    }

    public int describeContents() {
        return 0;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
