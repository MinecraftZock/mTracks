package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serial
import java.io.Serializable

open class GeoCode : Serializable, Parcelable {
    @SerializedName("results")
    @Expose
    var results: MutableList<Result?>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    protected constructor(`in`: Parcel) {
        `in`.readList(this.results!!, (Result::class.java.classLoader))
        this.status = (`in`.readValue((String::class.java.classLoader)) as String?)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(results)
        dest.writeValue(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GeoCode?> = object : Parcelable.Creator<GeoCode?> {
            override fun createFromParcel(`in`: Parcel): GeoCode {
                return GeoCode(`in`)
            }

            override fun newArray(size: Int): Array<GeoCode?> {
                return (arrayOfNulls<GeoCode>(size))
            }
        }

        @Serial
        private const val serialVersionUID = 3171710963124198211L
    }
}
