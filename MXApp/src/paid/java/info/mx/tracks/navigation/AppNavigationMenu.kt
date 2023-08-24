package info.mx.tracks.navigation

import android.content.Context
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView

class AppNavigationMenu : IAppNavigationMenu {

    override fun addFlavorItems(navigationView: NavigationView) = Unit

    override fun handleFlavorItem(context: Context, menuItem: MenuItem) = Unit

}
