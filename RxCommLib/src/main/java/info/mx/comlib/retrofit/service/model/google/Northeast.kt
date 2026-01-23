package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Northeast : Serializable, Parcelable {
    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lng")
    @Expose
    var lng: Double? = null

    protected constructor(`in`: Parcel) {
        this.lat = (`in`.readValue((Double::class.java.classLoader)) as Double?)
        this.lng = (`in`.readValue((Double::class.java.classLoader)) as Double?)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(lat)
        dest.writeValue(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Northeast?> = object : Parcelable.Creator<Northeast?> {
            override fun createFromParcel(`in`: Parcel): Northeast {
                return Northeast(`in`)
            }

            override fun newArray(size: Int): Array<Northeast?> {
                return arrayOfNulls(size)
            }
        }
    }
}
