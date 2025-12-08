package com.example.flowstate.features

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flowstate.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // the logo scale starts from smaller and grows to 1f after animation.
    val scale = remember { Animatable(0.7f) }

    // controls fade-in effect  for both logo and text
    val alpha = remember { Animatable(0f) }

    // to support both light and dark mode
    val colorScheme = MaterialTheme.colorScheme

    // this helps run animations and delays
    LaunchedEffect(Unit) {

        // logo grows smoothly from 70 % to 100 %
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )

        // logo and name appear gradually
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )

        delay(1500) // pauses

        // navigate to dashboard after animation is done
        navController.navigate("dashboard") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // app logo
            Image(
                painter = painterResource(id = R.drawable.flowstate),
                contentDescription = stringResource(id = R.string.app_logo),
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value) // logo grows
                    .alpha(alpha.value) // logo fades in
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(),
                color = colorScheme.onBackground.copy(alpha = alpha.value)
            )
        }
    }
}
