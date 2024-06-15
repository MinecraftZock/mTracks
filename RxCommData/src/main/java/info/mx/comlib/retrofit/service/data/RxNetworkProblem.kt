package info.mx.comlib.retrofit.service.data

import com.google.gson.annotations.SerializedName

// created by https://json2kt.com/

data class RxNetworkProblem (

    @SerializedName("androidid" ) var androidid : String? = null,
    @SerializedName("changed"   ) var changed   : Int?    = null,
    @SerializedName("id"        ) var id        : Int?    = null,
    @SerializedName("reason"    ) var reason    : String? = null,
    @SerializedName("tracks"    ) var tracks    : Int?    = null

)