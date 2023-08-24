
package info.mx.comlib.retrofit.service.data;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RxNetworkProblem implements Parcelable
{

    @SerializedName("androidid")
    @Expose
    private String androidid;
    @SerializedName("changed")
    @Expose
    private Integer changed;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("tracks")
    @Expose
    private Integer tracks;
    public final static Creator<RxNetworkProblem> CREATOR = new Creator<RxNetworkProblem>() {


        public RxNetworkProblem createFromParcel(android.os.Parcel in) {
            return new RxNetworkProblem(in);
        }

        public RxNetworkProblem[] newArray(int size) {
            return (new RxNetworkProblem[size]);
        }

    }
    ;

    @SuppressWarnings({
        "unchecked"
    })
    protected RxNetworkProblem(android.os.Parcel in) {
        this.androidid = ((String) in.readValue((String.class.getClassLoader())));
        this.changed = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.reason = ((String) in.readValue((String.class.getClassLoader())));
        this.tracks = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public RxNetworkProblem() {
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getTracks() {
        return tracks;
    }

    public void setTracks(Integer tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RxNetworkProblem.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("androidid");
        sb.append('=');
        sb.append(((this.androidid == null)?"<null>":this.androidid));
        sb.append(',');
        sb.append("changed");
        sb.append('=');
        sb.append(((this.changed == null)?"<null>":this.changed));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("reason");
        sb.append('=');
        sb.append(((this.reason == null)?"<null>":this.reason));
        sb.append(',');
        sb.append("tracks");
        sb.append('=');
        sb.append(((this.tracks == null)?"<null>":this.tracks));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.reason == null)? 0 :this.reason.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.androidid == null)? 0 :this.androidid.hashCode()));
        result = ((result* 31)+((this.tracks == null)? 0 :this.tracks.hashCode()));
        result = ((result* 31)+((this.changed == null)? 0 :this.changed.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RxNetworkProblem) == false) {
            return false;
        }
        RxNetworkProblem rhs = ((RxNetworkProblem) other);
        return ((((((this.reason == rhs.reason)||((this.reason!= null)&&this.reason.equals(rhs.reason)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.androidid == rhs.androidid)||((this.androidid!= null)&&this.androidid.equals(rhs.androidid))))&&((this.tracks == rhs.tracks)||((this.tracks!= null)&&this.tracks.equals(rhs.tracks))))&&((this.changed == rhs.changed)||((this.changed!= null)&&this.changed.equals(rhs.changed))));
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(androidid);
        dest.writeValue(changed);
        dest.writeValue(id);
        dest.writeValue(reason);
        dest.writeValue(tracks);
    }

    public int describeContents() {
        return  0;
    }

}
