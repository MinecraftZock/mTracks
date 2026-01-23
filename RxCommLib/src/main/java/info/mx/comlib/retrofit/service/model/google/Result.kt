package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Result : Serializable, Parcelable {
    @SerializedName("address_components")
    @Expose
    var addressComponents: MutableList<AddressComponent?>? = null

    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null

    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

    @SerializedName("place_id")
    @Expose
    var placeId: String? = null

    @SerializedName("types")
    @Expose
    var types: MutableList<String?>? = null

    protected constructor(`in`: Parcel) {
        `in`.readList(this.addressComponents!!, (AddressComponent::class.java.getClassLoader()))
        this.formattedAddress = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        this.geometry = (`in`.readValue((Geometry::class.java.getClassLoader())) as Geometry?)
        this.placeId = (`in`.readValue((String::class.java.getClassLoader())) as String?)
        `in`.readList(this.types!!, (String::class.java.getClassLoader()))
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(addressComponents)
        dest.writeValue(formattedAddress)
        dest.writeValue(geometry)
        dest.writeValue(placeId)
        dest.writeList(types)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Result?> = object : Parcelable.Creator<Result?> {
            override fun createFromParcel(`in`: Parcel): Result {
                return Result(`in`)
            }

            override fun newArray(size: Int): Array<Result?> {
                return (arrayOfNulls<Result>(size))
            }
        }
    }
}
