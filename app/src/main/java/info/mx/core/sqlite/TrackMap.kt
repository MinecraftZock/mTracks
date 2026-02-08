package info.mx.core.sqlite

import info.mx.tracks.common.SecHelper

class TrackMap(
    id: Long,
    latCrypt: Double,
    lonCrypt: Double,
    val access: String,
    val status: String
) : TracksDistanceSmall(id, latCrypt, lonCrypt) {
    val latDecrypt: Double
        get() = SecHelper.entcryptXtude(lat)

    val lonDecrypt: Double
        get() = SecHelper.entcryptXtude(lon)
}
