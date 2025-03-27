package com.example.jairosofttimesheet.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jairosofttimesheet.R
import com.example.jairosofttimesheet.ui.theme.JairosoftTimesheetTheme
import kotlinx.coroutines.delay

@Composable
fun StartUpScreen(navController: NavController? = null, isPreview: Boolean = false) {
    var visible by remember { mutableStateOf(false) }
    var fadeOutVisible by remember { mutableStateOf(false) }

    if (isPreview) {
        visible = true
    } else {
        LaunchedEffect(Unit) { visible = true }

        LaunchedEffect(Unit) {
            delay(3000)
            fadeOutVisible = true

            delay(500)
            navController?.navigate("LoginScreen") {
                popUpTo("StartUpScreen") { inclusive = true }
            }
        }


        // UI for the loading screen
        Box(
            modifier = Modifier
                .fillMaxSize() // Ensuring the Box takes up the entire screen
                .background(Color(0xFF10161F)), // the dark blue-ish background
            contentAlignment = Alignment.Center
        ) {
            // Define final target values
            val finalSize = 188.dp
            val finalOffsetX = 0.dp // Horizontal offset
            val finalOffsetY = (-230).dp // Vertical offset

            // Define initial values
            val initialSize = 250.dp
            val initialOffsetX = 0.dp
            val initialOffsetY = 0.dp

            val animatedSize by animateDpAsState(
                targetValue = if (visible) finalSize else initialSize,
                animationSpec = tween(durationMillis = 3000)
            )

            val animatedOffsetX by animateDpAsState(
                targetValue = if (visible) finalOffsetX else initialOffsetX,
                animationSpec = tween(durationMillis = 3000)
            )

            val animatedOffsetY by animateDpAsState(
                targetValue = if (visible) finalOffsetY else initialOffsetY,
                animationSpec = tween(durationMillis = 3000)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.timesheetapplogopng),
                    contentDescription = "Jairosoft Logo",
                    modifier = Modifier
                        .size(animatedSize) // Size animation
                        .offset(x = animatedOffsetX, y = animatedOffsetY) // Position animation
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartUpScreenPreview() {
    JairosoftTimesheetTheme {
        StartUpScreen(isPreview = true)
    }
}




