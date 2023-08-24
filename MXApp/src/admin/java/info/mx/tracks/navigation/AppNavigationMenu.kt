package info.mx.tracks.navigation

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import info.hannes.mxadmin.crashlytic.ActivityCrashlytic
import info.hannes.mxadmin.download.ActivityDownloadList
import info.hannes.mxadmin.location.ActivityLocationMonitor
import info.hannes.mxadmin.network.ActivityNetworkProblems

class AppNavigationMenu : IAppNavigationMenu {

    private val ADMINISTRATION: String = "Administration"

    override fun addFlavorItems(navigationView: NavigationView) {
        val menu = navigationView.menu
        menu.getItem(menu.size() - 1).title?.let {
            if (it != ADMINISTRATION) {
                val adminMenu = menu.addSubMenu(ADMINISTRATION)
                MXMenuItem.values().forEach {
                    adminMenu.add(it.title)
                }
            }
        }
    }

    override fun handleFlavorItem(context: Context, menuItem: MenuItem) {
        if (menuItem.title == MXMenuItem.MENU_LOCATION_MONITOR.title) {
            val qWfIntent = Intent(context, ActivityLocationMonitor::class.java)
            context.startActivity(qWfIntent)
        } else if (menuItem.title == MXMenuItem.MENU_IMPORT_ADMIN.title) {
            val qWfIntent = Intent(context, ActivityDownloadList::class.java)
            context.startActivity(qWfIntent)
        } else if (menuItem.title == MXMenuItem.MENU_NETWORK_PROBLEMS.title) {
            val qWfIntent = Intent(context, ActivityNetworkProblems::class.java)
            context.startActivity(qWfIntent)
        } else if (menuItem.title == MXMenuItem.MENU_CRASHLYTIC.title) {
            val qWfIntent = Intent(context, ActivityCrashlytic::class.java)
            context.startActivity(qWfIntent)
        } else {
            Toast.makeText(context, menuItem.title.toString() + " needs to be implemented", Toast.LENGTH_SHORT).show()
        }
    }

}
