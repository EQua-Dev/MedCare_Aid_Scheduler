/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.devstrike.app.medcareaidscheduler.data.BottomBarScreen
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorHouses
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorProfile
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorShifts
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorStaff

@Composable
fun SupervisorBottomNavigationGraph(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = Screen.SupervisorHouses.route) {
//        composable(
//            route = Screen.SupervisorHouses.route
//        ) {
//            SupervisorHouses(navController = navController)
//        }
//        composable(
//            route = Screen.SupervisorStaff.route
//        ) {
//            SupervisorStaff(navController = navController)
//        }
//        composable(
//            route = Screen.SupervisorShifts.route
//        ) {
//            SupervisorShifts(navController = navController)
//        }
//        composable(
//            route = Screen.SupervisorProfile.route
//        ) {
//            SupervisorProfile(navController = navController)
//        }
//    }
 NavHost(navController = navController, startDestination = BottomBarScreen.SupervisorHouses.route) {
        composable(
            route = BottomBarScreen.SupervisorHouses.route
        ) {
            SupervisorHouses(navController = navController)
        }
        composable(
            route = BottomBarScreen.SupervisorStaff.route
        ) {
            SupervisorStaff(navController = navController)
        }
        composable(
            route = BottomBarScreen.SupervisorShifts.route
        ) {
            SupervisorShifts(navController = navController)
        }
        composable(
            route = BottomBarScreen.SupervisorProfile.route
        ) {
            SupervisorProfile(navController = navController)
        }
    }

}