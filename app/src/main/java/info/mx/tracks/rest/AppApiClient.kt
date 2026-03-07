package info.mx.tracks.rest

import info.mx.comlib.AccessToken
import info.mx.comlib.RestServiceCreationHelper.createAuthenticatedService
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.comlib.service.AuthInterceptor
import info.mx.comlib.service.HostSelectionInterceptor
import info.mx.comlib.service.TokenAuthenticator
import okhttp3.logging.HttpLoggingInterceptor

class AppApiClient(logLevel: HttpLoggingInterceptor.Level) {

    val appTracksService: IAppTrackService

    init {
        appTracksService = getInternalTrackService(logLevel)
    }

    private fun getInternalTrackService(logLevel: HttpLoggingInterceptor.Level): IAppTrackService {
        val interceptorHostSelect = HostSelectionInterceptor()
        return createAuthenticatedService(
                IAppTrackService::class.java,
                CommLibPrefs.instance.serverUrl,
                TokenAuthenticator(AccessToken.instance),
                AuthInterceptor(AccessToken.instance),
                interceptorHostSelect, logLevel)
    }

}
