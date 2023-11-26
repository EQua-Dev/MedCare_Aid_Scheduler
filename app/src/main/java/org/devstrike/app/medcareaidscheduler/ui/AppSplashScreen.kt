/*
 * Copyright (c) 2023. 
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.ui.theme.Balsamiq
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AppSplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        ), label = ""
    )

    LaunchedEffect(key1 = true){
        startAnimation = true
        delay(3000)
        navController.popBackStack()
        navController.navigate(Screen.SignIn.route)
    }
    AnimatedSplashScreen(alpha = alphaAnim.value)

}

@Composable
fun AnimatedSplashScreen(alpha: Float) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .background(if (isSystemInDarkTheme()) Color.Black else MaterialTheme.colorScheme.primary)
            .fillMaxSize()){

        Text(
            text = stringResource(id = R.string.app_name),
            style = Typography.displayMedium,
            modifier = Modifier
                .alpha(alpha),
            textAlign = TextAlign.Center,
            fontFamily = Balsamiq
        )

    }
}