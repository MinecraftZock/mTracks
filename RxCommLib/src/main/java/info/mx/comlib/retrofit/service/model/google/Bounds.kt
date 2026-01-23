package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Bounds : Serializable, Parcelable {
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast? = null

    @SerializedName("southwest")
    @Expose
    var southwest: Southwest? = null

    protected constructor(`in`: Parcel) {
        this.northeast = (`in`.readValue((Northeast::class.java.classLoader)) as Northeast?)
        this.southwest = (`in`.readValue((Southwest::class.java.classLoader)) as Southwest?)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(northeast)
        dest.writeValue(southwest)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Bounds?> = object : Parcelable.Creator<Bounds?> {
            override fun createFromParcel(`in`: Parcel): Bounds {
                return Bounds(`in`)
            }

            override fun newArray(size: Int): Array<Bounds?> {
                return arrayOfNulls(size)
            }
        }
    }
}
