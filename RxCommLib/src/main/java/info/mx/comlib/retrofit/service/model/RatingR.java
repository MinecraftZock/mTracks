package info.mx.comlib.retrofit.service.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RatingR implements Serializable, Parcelable {

    @SerializedName("androidid")
    @Expose
    private String androidid;
    @SerializedName("approved")
    @Expose
    private Integer approved;
    @SerializedName("changed")
    @Expose
    private Integer changed = 0;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("deleted")
    @Expose
    private Integer deleted = 0;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rating")
    @Expose
    private Integer rating = 0;
    @SerializedName("trackId")
    @Expose
    private Integer trackId = 0;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("username")
    @Expose
    private String username;
    public final static Parcelable.Creator<RatingR> CREATOR = new Creator<RatingR>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RatingR createFromParcel(Parcel in) {
            RatingR instance = new RatingR();
            instance.androidid = ((String) in.readValue((String.class.getClassLoader())));
            instance.approved = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.changed = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.deleted = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.rating = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.trackId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.note = ((String) in.readValue((String.class.getClassLoader())));
            instance.username = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public RatingR[] newArray(int size) {
            return (new RatingR[size]);
        }

    };
    private final static long serialVersionUID = 6298068093067613146L;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(androidid);
        dest.writeValue(approved);
        dest.writeValue(changed);
        dest.writeValue(country);
        dest.writeValue(deleted);
        dest.writeValue(id);
        dest.writeValue(rating);
        dest.writeValue(trackId);
        dest.writeValue(username);
        dest.writeValue(note);
    }

    public int describeContents() {
        return 0;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
