package info.mx.comlib.util

import info.mx.comlib.retrofit.CommApiClient
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object RetroFileHelper {
    @Throws(IOException::class)
    fun downloadFile(apiClient: CommApiClient, urlToDownload: String, fileTo: String): Boolean {
        var res = false
        val call = apiClient.pictureService.downloadFileWithDynamicUrl(urlToDownload)
        val response = call.execute()
        if (response.code() == 200) {
            val file = File(fileTo)
            val fileOutputStream = FileOutputStream(file)
            IOUtils.write(response.body()!!.bytes(), fileOutputStream)
            res = true
        }
        return res
    }

    @Throws(IOException::class)
    fun getFileContent(apiClient: CommApiClient, urlToDownload: String): String {
        var res = ""
        val call = apiClient.pictureService.downloadFileWithDynamicUrl(urlToDownload)
        val response = call.execute()
        if (response.code() == 200) {
            res = response.body()!!.string()
        }
        return res
    }
}
