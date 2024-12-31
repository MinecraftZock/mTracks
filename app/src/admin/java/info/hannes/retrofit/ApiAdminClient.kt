package info.hannes.retrofit

import info.hannes.retrofit.service.IAdminService
import info.hannes.retrofit.service.ISurveillanceService
import info.mx.comlib.AccessToken
import info.mx.comlib.RestServiceCreationHelper
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.comlib.service.AuthInterceptor
import info.mx.comlib.service.HostSelectionInterceptor
import info.mx.comlib.service.TokenAuthenticator
import okhttp3.logging.HttpLoggingInterceptor

class ApiAdminClient(logLevel: HttpLoggingInterceptor.Level) {

    val surveillanceService: ISurveillanceService
    val adminService: IAdminService

    init {
        surveillanceService = getInternSurveillanceService(logLevel)
        adminService = getInternIAdminService(logLevel)
    }

    private fun getInternSurveillanceService(logLevel: HttpLoggingInterceptor.Level): ISurveillanceService {

        val interceptorHostSelect = HostSelectionInterceptor()

        return RestServiceCreationHelper.createAuthenticatedService(
                ISurveillanceService::class.java,
                CommLibPrefs.instance.serverUrl,
                TokenAuthenticator(AccessToken.instance),
                AuthInterceptor(AccessToken.instance),
                interceptorHostSelect, logLevel)
    }

    private fun getInternIAdminService(logLevel: HttpLoggingInterceptor.Level): IAdminService {

        val interceptorHostSelect = HostSelectionInterceptor()

        return RestServiceCreationHelper.createAuthenticatedService(
                IAdminService::class.java,
                CommLibPrefs.instance.serverUrl,
                TokenAuthenticator(AccessToken.instance),
                AuthInterceptor(AccessToken.instance),
                interceptorHostSelect, logLevel)
    }

}
