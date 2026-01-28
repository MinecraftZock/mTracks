@file:Suppress("ktlint:standard:filename")
package info.mx.tracks.koin

import info.hannes.mxadmin.service.DataManagerAdmin
import info.hannes.retrofit.ApiAdminClient
import info.mx.core.MxCoreApplication
import org.koin.dsl.module

val flavorModule = module {

    single {
        ApiAdminClient(MxCoreApplication.logLevel)
    }
    single {
        DataManagerAdmin(get())
    }

}
