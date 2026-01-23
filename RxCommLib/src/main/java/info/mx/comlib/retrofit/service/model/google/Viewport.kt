package info.mx.comlib.retrofit.service.model.google

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Viewport : Serializable, Parcelable {
    @SerializedName("northeast")
    @Expose
    var northeast: Northeast_? = null

    @SerializedName("southwest")
    @Expose
    var southwest: Southwest_? = null

    protected constructor(`in`: Parcel) {
        this.northeast = (`in`.readValue((Northeast_::class.java.classLoader)) as Northeast_?)
        this.southwest = (`in`.readValue((Southwest_::class.java.classLoader)) as Southwest_?)
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
        val CREATOR: Parcelable.Creator<Viewport?> = object : Parcelable.Creator<Viewport?> {
            override fun createFromParcel(`in`: Parcel): Viewport {
                return Viewport(`in`)
            }

            override fun newArray(size: Int): Array<Viewport?> {
                return arrayOfNulls(size)
            }
        }
    }
}
