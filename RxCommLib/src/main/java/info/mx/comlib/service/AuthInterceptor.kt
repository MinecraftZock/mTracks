package info.mx.comlib.service

import info.mx.comlib.AccessToken
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(var access: AccessToken) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (access.accessToken == null && access.refreshToken == null) {
            return chain.proceed(chain.request())
        }
        val originalRequest: Request = chain.request()
        return chain.proceed(addAuthorizationHeader(originalRequest, access))
    }

    companion object {
        private const val AUTHORIZATION = "Authorization"

        @JvmStatic
        fun addAuthorizationHeader(request: Request, access: AccessToken): Request {
            // Add authorization header with updated authorization value to intercepted request
            return request.newBuilder()
                .header(AUTHORIZATION, "Bearer " + access.accessToken)
                .build()
        }
    }
}