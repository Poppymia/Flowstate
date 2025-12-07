package com.example.flowstate.features


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flowstate.R
import kotlinx.coroutines.delay
import java.nio.file.Files.copy
import java.util.Collections.copy
import  android.R.id.copy

@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f)}

LaunchedEffect(true) {

    scale.animateTo(
        targetValue = 1f,
        animationSpec = tween (durationMillis = 800, easing = FastOutSlowInEasing)
    )
    alpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 600)
    )

    delay(1500)

    navController.navigate("dashboard"){
        popUpTo("splash") { inclusive = true }
    }
}

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFFFFF)),
        contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id =R.drawable.flowstate),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.Text(
                "Flowstate",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black.copy(alpha = alpha.value)
            )

    }


    }
}
