package info.mx.comlib

class AccessToken private constructor() {

    var accessToken: String? = null

    var refreshToken: String? = null

    companion object {
        private var ourInstance: AccessToken? = null

        val instance: AccessToken
            get() {
                if (ourInstance == null) {
                    ourInstance = AccessToken()
                }
                return ourInstance!!
            }
    }
}
