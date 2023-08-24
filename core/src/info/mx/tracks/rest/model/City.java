
package info.mx.tracks.rest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class City implements Serializable, Parcelable {

    @SerializedName("geoname_id")
    @Expose
    private Integer geonameId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("iso2")
    @Expose
    private String iso2;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("population")
    @Expose
    private Integer population;
    public final static Parcelable.Creator<City> CREATOR = new Creator<City>() {


        @SuppressWarnings({
                "unchecked"
        })
        public City createFromParcel(Parcel in) {
            City instance = new City();
            instance.geonameId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.lat = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.lon = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.country = ((String) in.readValue((String.class.getClassLoader())));
            instance.iso2 = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.population = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public City[] newArray(int size) {
            return (new City[size]);
        }

    };
    private final static long serialVersionUID = 2727085655039238373L;

    public Integer getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(geonameId);
        dest.writeValue(name);
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeValue(country);
        dest.writeValue(iso2);
        dest.writeValue(type);
        dest.writeValue(population);
    }

    public int describeContents() {
        return 0;
    }

}
