package info.mx.comAdminlib.retrofit.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Approved {
    @SerializedName("id")
    @Expose
    var id: Long = 0

    @SerializedName("status")
    @Expose
    var status = 0

    @SerializedName("changeuser")
    @Expose
    var changeuser: String? = null
}
