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
import org.checkerframework.common.subtyping.qual.Bottom
import org.devstrike.app.medcareaidscheduler.data.BottomBarScreen
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorHouses
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorProfile
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorShifts
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorStaff

@Composable
fun SupervisorBottomNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomBarScreen.Houses.route) {
        composable(
            route = BottomBarScreen.Houses.route
        ) {
            SupervisorHouses(navController = navController)
        }
        composable(
            route = BottomBarScreen.Staff.route
        ) {
            SupervisorStaff(navController = navController)
        }
        composable(
            route = BottomBarScreen.Shifts.route
        ) {
            SupervisorShifts(navController = navController)
        }
        composable(
            route = BottomBarScreen.Profile.route
        ) {
            SupervisorProfile(navController = navController)
        }
    }

}