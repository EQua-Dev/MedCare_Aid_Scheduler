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
import org.devstrike.app.medcareaidscheduler.ui.AppSplashScreen
import org.devstrike.app.medcareaidscheduler.ui.auth.CreateNewUser
import org.devstrike.app.medcareaidscheduler.ui.auth.ResetPassword
import org.devstrike.app.medcareaidscheduler.ui.auth.SignIn
import org.devstrike.app.medcareaidscheduler.ui.staff.StaffHome
import org.devstrike.app.medcareaidscheduler.ui.supervisor.SupervisorHome
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses.SupervisorAddHouseSheet

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route ){
        composable(
            route = Screen.Splash.route
        ){
            AppSplashScreen(navController = navController)
        }
        composable(
            route = Screen.SignIn.route
        ){
            SignIn(navController = navController)
        }
        composable(
            route = Screen.CreateUser.route
        ){
            CreateNewUser(navController = navController)
        }
        composable(
            route = Screen.ForgotPassword.route
        ){
            ResetPassword(navController = navController)
        }
        composable(
            route = Screen.SupervisorLanding.route
        ){
            SupervisorHome(baseNavHostController = navController)
        }
        composable(
            route = Screen.StaffLanding.route
        ){
            StaffHome(baseNavHostController = navController)
        }
        /*composable(route = Screen.AddNewHouse.route){
            SupervisorAddHouseSheet(navController = navController)
        }*/
    }
}