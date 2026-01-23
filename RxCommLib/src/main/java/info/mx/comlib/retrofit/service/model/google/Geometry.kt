package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Geometry : Serializable, Parcelable {
    @SerializedName("bounds")
    @Expose
    var bounds: Bounds? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("location_type")
    @Expose
    var locationType: String? = null

    @SerializedName("viewport")
    @Expose
    var viewport: Viewport? = null

    protected constructor(`in`: Parcel) {
        this.bounds = (`in`.readValue((Bounds::class.java.classLoader)) as Bounds?)
        this.location = (`in`.readValue((Location::class.java.classLoader)) as Location?)
        this.locationType = (`in`.readValue((String::class.java.classLoader)) as String?)
        this.viewport = (`in`.readValue((Viewport::class.java.classLoader)) as Viewport?)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(bounds)
        dest.writeValue(location)
        dest.writeValue(locationType)
        dest.writeValue(viewport)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Geometry?> = object : Parcelable.Creator<Geometry?> {
            override fun createFromParcel(`in`: Parcel): Geometry {
                return Geometry(`in`)
            }

            override fun newArray(size: Int): Array<Geometry?> {
                return arrayOfNulls(size)
            }
        }
    }
}
