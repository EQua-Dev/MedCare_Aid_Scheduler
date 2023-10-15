/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.devstrike.app.medcareaidscheduler.data.BottomBarScreen
import org.devstrike.app.medcareaidscheduler.navigation.StaffBottomNavigationGraph
import org.devstrike.app.medcareaidscheduler.navigation.SupervisorBottomNavigationGraph
import org.devstrike.app.medcareaidscheduler.ui.supervisor.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHome(baseNavHostController: NavHostController) {
    val navController = rememberNavController()


    Scaffold(bottomBar = {
        StaffBottomBar(navController = navController)
    }, topBar = {

        Text(text = "Staff Top Bar")
    }) {
        StaffBottomNavigationGraph(navController = navController)
    }

}

@Composable
fun StaffBottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.StaffShifts,
        BottomBarScreen.StaffNotifications,
        BottomBarScreen.StaffPayments,
        BottomBarScreen.StaffProfile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            StaffAddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }
}

@Composable
fun RowScope.StaffAddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = {
            Text(text = screen.title)
        },
        onClick = { navController.navigate(screen.route) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}