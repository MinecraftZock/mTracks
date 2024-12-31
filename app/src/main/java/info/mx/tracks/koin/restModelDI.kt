package info.mx.tracks.koin

import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxCoreApplication
import org.koin.dsl.module

val restModule = module {

    single {
        CommApiClient(MxCoreApplication.logLevel)
    }

}
