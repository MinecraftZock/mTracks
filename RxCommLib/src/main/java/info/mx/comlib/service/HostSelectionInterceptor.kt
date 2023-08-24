package info.mx.comlib.service

import info.mx.comlib.prefs.CommLibPrefs
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HostSelectionInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var host = CommLibPrefs.instance.serverUrl
        if (host.indexOf("/", 10) > -1) {
            host = host.substring(0, host.indexOf("/", 10))
        }
        val scheme = host.substring(0, host.indexOf("//") - 1)
        host = host.substring(host.indexOf("//") + 2)
        var port = 80
        if (scheme.endsWith("s")) {
            port = 443
        }
        if (host.contains(":")) {
            port = host.substring(host.indexOf(":") + 1).toInt()
        }
        val newUrl = request.url.newBuilder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .build()
        request = request.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(request)
    }
}
