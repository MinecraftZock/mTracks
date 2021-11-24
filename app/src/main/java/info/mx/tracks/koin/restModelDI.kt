@file:Suppress("ktlint:standard:filename")
package info.mx.tracks.koin

import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.rest.AppApiClient
import org.koin.dsl.module

val restModule = module {

    single {
        CommApiClient(MxCoreApplication.logLevel)
    }
    single {
        AppApiClient(MxCoreApplication.logLevel)
    }

}
