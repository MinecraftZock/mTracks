package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class AddressComponent : Serializable, Parcelable {
    @SerializedName("long_name")
    @Expose
    var longName: String? = null

    @SerializedName("short_name")
    @Expose
    var shortName: String? = null

    @SerializedName("types")
    @Expose
    var types: MutableList<String?>? = null

    protected constructor(`in`: Parcel) {
        this.longName = (`in`.readValue((String::class.java.classLoader)) as String?)
        this.shortName = (`in`.readValue((String::class.java.classLoader)) as String?)
        `in`.readList(this.types!!, (String::class.java.classLoader))
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(longName)
        dest.writeValue(shortName)
        dest.writeList(types)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AddressComponent?> =
            object : Parcelable.Creator<AddressComponent?> {
                override fun createFromParcel(`in`: Parcel): AddressComponent {
                    return AddressComponent(`in`)
                }

                override fun newArray(size: Int): Array<AddressComponent?> {
                    return (arrayOfNulls<AddressComponent>(size))
                }
            }
    }
}
