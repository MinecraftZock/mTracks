package info.hannes.mxadmin.data.brothers

class Attributes {
    var city: String? = null
    var city_rendered: String? = null
    var description: String? = null
    var field_map: String? = null
    var field_map_1: String? = null
    var field_map_1_rendered: String? = null
    var field_map_rendered: String? = null
    var field_track_photos: String? = null
    var field_track_photos_rendered: String? = null
    var name: String? = null
    var title: String? = null
    var title_rendered: String? = null
    val url: String
        get() {
            val tok1 = name!!.substring(name!!.indexOf("\"/") + 1)
            return tok1.substring(0, tok1.indexOf("\""))
        }
    val photo: String
        get() {
            val tok1 = field_track_photos!!.substring(field_track_photos!!.indexOf("http://"))
            return tok1.substring(0, tok1.indexOf(".jpg") + 4)
        }
}