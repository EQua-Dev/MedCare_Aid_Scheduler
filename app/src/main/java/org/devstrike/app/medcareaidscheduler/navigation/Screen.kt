/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.navigation

sealed class Screen(val route: String){
    object Splash: Screen(route = "splash_screen")
    object SignIn: Screen(route = "sign_in_screen")
    object ForgotPassword: Screen(route = "forgot_password_screen")
    object SupervisorLanding: Screen(route = "supervisor_landing_screen")
    object AddNewHouse: Screen(route = "add_new_house_screen")
    object StaffLanding: Screen(route = "staff_landing_screen")
    object SupervisorHouses: Screen(route = "supervisor_houses_screen")
    object SupervisorStaff: Screen(route = "supervisor_staff_screen")
    object SupervisorShifts: Screen(route = "supervisor_shifts_screen")
    object SupervisorProfile: Screen(route = "supervisor_profile_screen")


}
