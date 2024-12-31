package info.hannes.retrofit.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PictureResponse {
    @SerializedName("id")
    @Expose
    var id = 0
    @SerializedName("changed")
    @Expose
    var changed: Long = 0
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("aktion")
    @Expose
    var aktion: String? = null

}