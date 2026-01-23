package info.hannes.retrofit.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatusResponse {
    @get:SerializedName("aktion")
    @Expose
    var changed: Long = 0

    @SerializedName("message")
    @Expose
    var message: String? = null

}
