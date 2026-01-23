package info.hannes.mechadmin.brothers

class Features {
    var attributes: Attributes? = null
    var projection: String? = null
    var wkt: String? = null
    val lat: Double
        get() {
            val wgt = wkt!!.replace("POINT(", "").replace(")", "")
            val coord = wgt.split(" ").toTypedArray()
            return coord[1].toDouble()
        }
    val lon: Double
        get() {
            val wgt = wkt!!.replace("POINT(", "").replace(")", "")
            val coord = wgt.split(" ").toTypedArray()
            return coord[0].toDouble()
        }
}
