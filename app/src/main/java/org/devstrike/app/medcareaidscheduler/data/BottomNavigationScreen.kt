/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import org.devstrike.app.medcareaidscheduler.R

data class BottomNavigationScreen(
    val title: String = "",
    val selectedItem: Unit,
    val unSelectedItem: Unit,
    val notificationCount: Int? = null,
    val route: String = ""
)

sealed class BottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
){
    object Houses: BottomBarScreen(
        title = "Houses",
        icon = Icons.Default.House,
        route = "supervisor_houses"
    )
    object Staff: BottomBarScreen(
        title = "Staff",
        icon = Icons.Default.Badge,
        route = "supervisor_staff"
    )
    object Shifts: BottomBarScreen(
        title = "Shifts",
        icon = Icons.Default.Schedule,
        route = "supervisor_shifts"
    )
    object Profile: BottomBarScreen(
        title = "Profile",
        icon = Icons.Default.Person,
        route = "supervisor_profile"
    )
}
