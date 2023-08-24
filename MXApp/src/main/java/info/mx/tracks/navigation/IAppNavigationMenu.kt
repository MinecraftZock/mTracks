package info.mx.tracks.navigation

import android.content.Context
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView

interface IAppNavigationMenu {

    fun addFlavorItems(navigationView: NavigationView)

    fun handleFlavorItem(context: Context, menuItem: MenuItem)

}
