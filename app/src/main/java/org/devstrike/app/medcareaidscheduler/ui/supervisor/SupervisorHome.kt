/*
 * Copyright (c) 2023. 
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.devstrike.app.medcareaidscheduler.data.BottomBarScreen
import org.devstrike.app.medcareaidscheduler.navigation.SupervisorBottomNavigationGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorHome(baseNavHostController: NavHostController) {
    val navController = rememberNavController()


    Scaffold(bottomBar = {
        BottomBar(navController = navController)
    }, topBar = {

        Text(text = "Supervisor Top Bar")
    }) {innerPadding ->
        SupervisorBottomNavigationGraph(navController = navController)
    }

//    navController = rememberNavController()
//    SupervisorBottomNavigationGraph(navController = navController)
//    val bottomNavItems = listOf(
//        BottomNavigationScreen(
//            title = stringResource(id = R.string.houses_title),
//            selectedItem = Image(
//                painterResource(id = R.drawable.ic_houses_filled),
//                contentDescription = stringResource(id = R.string.houses_title)
//            ),
//            unSelectedItem = Image(
//                painterResource(id = R.drawable.ic_houses_outlined),
//                contentDescription = stringResource(id = R.string.houses_title)
//            ),
//            route = Screen.SupervisorHouses.route
//        ),
//        BottomNavigationScreen(
//            title = stringResource(id = R.string.staff_title),
//            selectedItem = Image(
//                painterResource(id = R.drawable.ic_staff_filled),
//                contentDescription = stringResource(id = R.string.staff_title)
//            ),
//            unSelectedItem = Image(
//                painterResource(id = R.drawable.ic_staff_outlined),
//                contentDescription = stringResource(id = R.string.staff_title)
//            ),
//            route = Screen.SupervisorStaff.route
//        ),
//        BottomNavigationScreen(
//            title = stringResource(id = R.string.shift_title),
//            selectedItem = Image(
//                painterResource(id = R.drawable.ic_shift_filled),
//                contentDescription = stringResource(id = R.string.shift_title)
//            ),
//            unSelectedItem = Image(
//                painterResource(id = R.drawable.ic_shift_outlined),
//                contentDescription = stringResource(id = R.string.shift_title)
//            ),
//            route = Screen.SupervisorShifts.route
//        ),
//        BottomNavigationScreen(
//            title = stringResource(id = R.string.profile_title),
//            selectedItem = Image(
//                painterResource(id = R.drawable.ic_profile_filled),
//                contentDescription = stringResource(id = R.string.profile_title)
//            ),
//            unSelectedItem = Image(
//                painterResource(id = R.drawable.ic_profile_outlined),
//                contentDescription = stringResource(id = R.string.profile_title)
//            ),
//            route = Screen.SupervisorProfile.route
//        )
//    )
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
//
//    var selectedIndex by rememberSaveable {
//        mutableIntStateOf(0)
//    }
//
//
//        Scaffold(
//            bottomBar = {
//                NavigationBar {
//                    bottomNavItems.forEachIndexed { index, item ->
//                        NavigationBarItem(
//                            selected = currentDestination?.hierarchy?.any {
//                                it.route == item.route
////                                selectedIndex == index
//                            } == true,
//                            onClick = {
//                                selectedIndex = index
//                                navController.navigate(item.route)
//                            },
//                            label = {
//                                Text(text = item.title)
//                            },
//                            icon = {
//                                BadgedBox(badge = {
//                                    if (item.notificationCount != null) {
//                                        Badge {
//                                            Text(text = item.notificationCount.toString())
//                                        }
//                                    }
//                                }) {
//                                    if (index == selectedIndex) {
//                                        item.selectedItem
//                                    } else item.unSelectedItem
//                                }
//                            })
//
//                    }
//                }
//            }
//        ) {
//
//            Box(modifier = Modifier.padding(it))
//
//        }

}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.SupervisorHouses,
        BottomBarScreen.SupervisorStaff,
        BottomBarScreen.SupervisorShifts,
        BottomBarScreen.SupervisorProfile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }
}

@Composable
fun RowScope.AddItem(
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
        onClick = { navController.navigate(screen.route){
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            }},
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}