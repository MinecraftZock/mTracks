package info.hannes.mechadmin

import info.hannes.mechadmin_gen.rest.MxAdmin
import info.hannes.mechadmin_gen.rest.MxCal
import info.mx.comlib.prefs.CommLibPrefs
import info.mx.tracks.BuildConfig
import info.mx.tracks.common.LoginParam

class WebClient private constructor() {
    fun setHeader(params: List<LoginParam>) {
        for (param in params) {
            mxCal!!.setHeader(param.key, param.value)
            mxAdmin!!.setHeader(param.key, param.value)
        }
    }

    companion object {
        var mxCal: MxCal? = null
            private set

        var mxAdmin: MxAdmin? = null
            private set
        private val ourInstance = WebClient()

        @JvmStatic
        val instance: WebClient
            get() {
                if (mxCal == null) {
                    createApiClients()
                }
                return ourInstance
            }

        private fun createApiClients() {
            mxCal = MxCal(CommLibPrefs.instance.serverUrl, BuildConfig.DEBUG)
            mxAdmin = MxAdmin(CommLibPrefs.instance.serverUrl, BuildConfig.DEBUG)
        }
    }
}