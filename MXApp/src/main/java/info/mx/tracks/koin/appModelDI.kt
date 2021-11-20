package info.mx.tracks.koin

import info.mx.tracks.navigation.AppNavigationMenu
import info.mx.tracks.tools.AddMobHelper
import info.mx.tracks.tools.PermissionHelper
import info.mx.tracks.trackdetail.comment.CommentRepository
import info.mx.tracks.trackdetail.comment.CommentViewModel
import org.koin.dsl.module

val appModule = module {
    // single instance
    single { PermissionHelper(get()) }
    single { AppNavigationMenu() }
    single { AddMobHelper(get()) }
    single { CommentViewModel() }
    single { CommentRepository() }

    //factory { MySimplePresenter(get()) }
}
