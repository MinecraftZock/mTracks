package info.mx.comlib.retrofit

import info.mx.comlib.AccessToken.Companion.instance
import info.mx.comlib.RestServiceCreationHelper.createAuthenticatedService
import info.mx.comlib.RestServiceCreationHelper.createService
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.comlib.retrofit.service.IGoogleService
import info.mx.comlib.retrofit.service.IPictureService
import info.mx.comlib.retrofit.service.ITrackService
import info.mx.comlib.service.AuthInterceptor
import info.mx.comlib.service.HostSelectionInterceptor
import info.mx.comlib.service.TokenAuthenticator
import okhttp3.logging.HttpLoggingInterceptor

class CommApiClient(logLevel: HttpLoggingInterceptor.Level) {
    val pictureService: IPictureService
    val tracksService: ITrackService
    val googleService: IGoogleService
    private fun getInternalPictureService(logLevel: HttpLoggingInterceptor.Level): IPictureService {
        val interceptorHostSelect = HostSelectionInterceptor()
        return createAuthenticatedService(
            IPictureService::class.java,
            CommLibPrefs.instance.serverUrl,
            TokenAuthenticator(instance),
            AuthInterceptor(instance),
            interceptorHostSelect, logLevel
        )
    }

    private fun getInternalTrackService(logLevel: HttpLoggingInterceptor.Level): ITrackService {
        val interceptorHostSelect = HostSelectionInterceptor()
        return createAuthenticatedService(
            ITrackService::class.java,
            CommLibPrefs.instance.serverUrl,
            TokenAuthenticator(instance),
            AuthInterceptor(instance),
            interceptorHostSelect, logLevel
        )
    }

    private fun getInternIGoogleService(logLevel: HttpLoggingInterceptor.Level): IGoogleService {
        return createService(IGoogleService::class.java, "https://maps.googleapis.com", logLevel)
    }

    init {
        pictureService = getInternalPictureService(logLevel)
        tracksService = getInternalTrackService(logLevel)
        googleService = getInternIGoogleService(logLevel)
    }
}
