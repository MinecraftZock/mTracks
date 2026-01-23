package info.mx.comlib.service

import info.mx.comlib.AccessToken
import info.mx.comlib.service.AuthInterceptor.Companion.addAuthorizationHeader
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(var access: AccessToken) : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request {
        //		Intent startLoginIntent = new Intent(context, LoginActivity.class);
        //		startLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //		context.startActivity(startLoginIntent);
        //TODO: Refresh tokening is not yet implemented from Backend Authenticator

        //		Call<AccessToken> refreshCall = loginService.refreshAccess(access);
        //		AccessToken newAccess = refreshCall.execute().body();
//        AccessToken newAccess = new AccessToken();
//        access.fill(newAccess);
        return addAuthorizationHeader(response.request, access)
    }
}
