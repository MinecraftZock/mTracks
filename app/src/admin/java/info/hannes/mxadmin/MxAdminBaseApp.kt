package info.hannes.mxadmin

import android.content.Context
import info.hannes.mechadmin.WebClient
import info.hannes.mxadmin.service.DataManagerAdmin
import info.mx.tracks.MxApplication
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject

abstract class MxAdminBaseApp : MxApplication() {

    private val dataManagerAdmin: DataManagerAdmin by inject()

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        val applicationScope = MainScope()
        applicationScope.launch {
            createApiClients()
        }
    }

    private fun createApiClients() {
        WebClient.instance.setHeader(aadhresUParams)
    }

    override fun confirmPicture(context: Context, restId: Long, statusCurrent: Int) {
        ApproveImageAction.confirmPicture(context, restId, statusCurrent, dataManagerAdmin)
    }

    companion object {

        private var sInstance: MxAdminBaseApp? = null

        /**
         * Returns the application instance.
         *
         * @return app instance
         */
        fun get(): MxAdminBaseApp? {
            return sInstance
        }
    }
}
