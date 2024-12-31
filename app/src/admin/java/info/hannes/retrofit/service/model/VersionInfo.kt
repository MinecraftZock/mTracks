package info.hannes.retrofit.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VersionInfo {
    @SerializedName("restVersion")
    @Expose
    var restVersion: String? = null
    @SerializedName("dbVersion")
    @Expose
    var dbVersion: String? = null
    @SerializedName("dbName")
    @Expose
    val dbName: String? = null

}