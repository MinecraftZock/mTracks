package info.mx.tracks.util

import info.mx.tracks.ops.UnexpectedHttpStatusException
import retrofit2.Response

private const val HTTP_OK = 200

fun Response<*>.checkResponseCodeOk() {
    if (this.code() != HTTP_OK) {
        throw UnexpectedHttpStatusException(this.code(), HTTP_OK)
    }
}
