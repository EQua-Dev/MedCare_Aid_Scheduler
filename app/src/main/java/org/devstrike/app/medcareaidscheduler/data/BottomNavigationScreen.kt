/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

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
    object SupervisorHouses: BottomBarScreen(
        title = "Houses",
        icon = Icons.Default.House,
        route = "supervisor_houses"
    )
    object SupervisorStaff: BottomBarScreen(
        title = "Staff",
        icon = Icons.Default.Badge,
        route = "supervisor_staff"
    )
    object SupervisorShifts: BottomBarScreen(
        title = "Shifts",
        icon = Icons.Default.Schedule,
        route = "supervisor_shifts"
    )
    object SupervisorProfile: BottomBarScreen(
        title = "Profile",
        icon = Icons.Default.Person,
        route = "supervisor_profile"
    )
    object StaffShifts : BottomBarScreen(
        title = "Shifts",
        icon = Icons.Default.Schedule,
        route = "staff_shifts"
    )
    object StaffNotifications : BottomBarScreen(
        title = "Notification",
        icon = Icons.Default.MarkChatUnread,
        route = "staff_notifications"
    )
    object StaffPayments : BottomBarScreen(
        title = "Payments",
        icon = Icons.Default.Payments,
        route = "staff_payments"
    )
    object StaffProfile : BottomBarScreen(
        title = "Profile",
        icon = Icons.Default.Person,
        route = "staff_profile"
    )
}
