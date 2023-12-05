/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.devstrike.app.medcareaidscheduler.navigation.BottomBarScreen
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.navigation.StaffBottomNavigationGraph
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHome(baseNavHostController: NavHostController) {
    val navController = rememberNavController()
    val TAG = "SupervisorHome"
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(false) }
    val userDetail = Common.auth.uid?.let { getUser(it, context) }




    Scaffold(bottomBar = {
        StaffBottomBar(navController = navController)
    }, topBar = {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hello, ${userDetail?.userFirstName}",
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(4.dp),
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.clickable {
                    openDialog.value = true
                })
            }
        }

    }) {innerPadding ->

        Box(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            StaffBottomNavigationGraph(navController = navController)
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            title = {
                Text(text = "Logout", style = Typography.titleLarge)
            },
            text = {
                Text(text = "Do you want to logout?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Common.auth.signOut()
                        baseNavHostController.navigate(Screen.SignIn.route)
                        openDialog.value = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("No")
                }
            },

            )
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