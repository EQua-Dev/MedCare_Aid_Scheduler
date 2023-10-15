/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import org.devstrike.app.medcareaidscheduler.navigation.Screen

@Composable
fun SupervisorStaff(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Magenta), contentAlignment = Alignment.Center){

        Text(text = "Supervisor Staff")
    }

}