package info.mx.comlib

import com.google.gson.Gson
import com.google.gson.GsonBuilder

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RestServiceCreationHelper {

    private var httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()

    init {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }

    private fun createGson(): Gson {
        return GsonBuilder().serializeNulls()
            .create()
    }

    fun <T> createAuthenticatedService(
        retrofitInterface: Class<T>,
        baseUrl: String, authenticator: Authenticator, authInterceptor: Interceptor, interceptorHostSelect: Interceptor,
        logLevel: HttpLoggingInterceptor.Level
    ): T {
        var baseUrlInternal = baseUrl

        httpLoggingInterceptor.level = logLevel
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(authInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(interceptorHostSelect)
            .authenticator(authenticator)
            .build()

        val gson = createGson()

        if (baseUrlInternal.indexOf("/", 10) > -1) {
            baseUrlInternal = baseUrlInternal.substring(0, baseUrlInternal.indexOf("/", 10))
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlInternal)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(retrofitInterface)
    }

    fun <T> createService(retrofitInterface: Class<T>, baseUrl: String, logLevel: HttpLoggingInterceptor.Level): T {
        var baseUrlInternal = baseUrl

        httpLoggingInterceptor.level = logLevel
        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val gson = createGson()

        if (baseUrlInternal.indexOf("/", 10) > -1) {
            baseUrlInternal = baseUrlInternal.substring(0, baseUrlInternal.indexOf("/", 10))
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrlInternal)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(retrofitInterface)
    }

}
