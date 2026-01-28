@file:Suppress("ktlint:standard:filename")

package info.mx.core.koin

import info.mx.comlib.retrofit.CommApiClient
import info.mx.core.MxCoreApplication
import info.mx.core.data.DataManagerCore
import org.koin.dsl.module

val coreModule = module {

    single {
        CommApiClient(MxCoreApplication.logLevel)
    }
    single {
        DataManagerCore(get())
    }

}
