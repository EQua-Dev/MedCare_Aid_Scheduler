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
import org.devstrike.app.medcareaidscheduler.ui.staff.StaffNotifications
import org.devstrike.app.medcareaidscheduler.ui.staff.StaffPayments
import org.devstrike.app.medcareaidscheduler.ui.staff.StaffProfile
import org.devstrike.app.medcareaidscheduler.ui.staff.StaffShifts
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorHouses
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorProfile
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorShifts
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorStaff

@Composable
fun StaffBottomNavigationGraph(navController: NavHostController) {
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
 NavHost(navController = navController, startDestination = BottomBarScreen.StaffShifts.route) {
        composable(
            route = BottomBarScreen.StaffShifts.route
        ) {
            StaffShifts(navController = navController)
        }
        composable(
            route = BottomBarScreen.StaffNotifications.route
        ) {
            StaffNotifications(navController = navController)
        }
        composable(
            route = BottomBarScreen.StaffPayments.route
        ) {
            StaffPayments(navController = navController)
        }
        composable(
            route = BottomBarScreen.StaffProfile.route
        ) {
            StaffProfile(navController = navController)
        }
    }

}