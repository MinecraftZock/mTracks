package info.mx.tracks.koin

import info.mx.comlib.retrofit.CommApiClient
import info.mx.tracks.MxCoreApplication
import info.mx.tracks.data.DataManagerCore
import org.koin.dsl.module

val coreModule = module {

    single {
        CommApiClient(MxCoreApplication.logLevel)
    }
    single {
        DataManagerCore(get())
    }

}
