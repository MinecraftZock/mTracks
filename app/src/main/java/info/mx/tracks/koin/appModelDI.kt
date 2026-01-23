@file:Suppress("ktlint:standard:filename")
package info.mx.tracks.koin

import info.mx.tracks.data.DataManagerApp
import info.mx.tracks.navigation.AppNavigationMenu
import info.mx.tracks.tools.AddMobHelper
import info.mx.tracks.tools.PermissionHelper
import org.koin.dsl.module

val appModule = module {
    // single instance
    single { PermissionHelper(get()) }
    single { AppNavigationMenu() }
    single { AddMobHelper(get()) }

    single { DataManagerApp(get()) }

    //factory { MySimplePresenter(get()) }
}
