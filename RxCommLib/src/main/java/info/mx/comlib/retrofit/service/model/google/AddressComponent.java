
package info.mx.comlib.retrofit.service.model.google;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AddressComponent implements Serializable, Parcelable {

    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    public final static Creator<AddressComponent> CREATOR = new Creator<AddressComponent>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AddressComponent createFromParcel(Parcel in) {
            return new AddressComponent(in);
        }

        public AddressComponent[] newArray(int size) {
            return (new AddressComponent[size]);
        }

    };
    private final static long serialVersionUID = 7223519685451816384L;

    protected AddressComponent(Parcel in) {
        this.longName = ((String) in.readValue((String.class.getClassLoader())));
        this.shortName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.types, (String.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public AddressComponent() {
    }

    /**
     * @param longName
     * @param types
     * @param shortName
     */
    public AddressComponent(String longName, String shortName, List<String> types) {
        super();
        this.longName = longName;
        this.shortName = shortName;
        this.types = types;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(longName);
        dest.writeValue(shortName);
        dest.writeList(types);
    }

    public int describeContents() {
        return 0;
    }

}
