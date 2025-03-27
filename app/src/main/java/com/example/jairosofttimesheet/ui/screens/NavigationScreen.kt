package com.example.jairosofttimesheet.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jairosofttimesheet.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NavigationScreen(
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}

@Composable
fun TopBar(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var isLogoutClicked by remember { mutableStateOf(false) }
    val logoutScale by animateFloatAsState(if (isLogoutClicked) 1.1f else 1f, label = "LogoutScale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.timesheetappbanner),
            contentDescription = "Jairosoft Logo Banner",
            modifier = Modifier.size(158.dp, 48.dp),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.logout),
            contentDescription = "Logout",
            modifier = Modifier
                .size(28.dp, 28.dp)
                .clickable {
                    isLogoutClicked = true
                    navController.navigate("LoginScreen")
                    coroutineScope.launch {
                        delay(300)
                        isLogoutClicked = false
                    }
                }
                .scale(logoutScale)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var isTimesheetClicked by remember { mutableStateOf(false) }
    var isHomeClicked by remember { mutableStateOf(false) }
    var isAttendanceClicked by remember { mutableStateOf(false) }

    val timesheetScale by animateFloatAsState(if (isTimesheetClicked) 1.1f else 1f, label = "TimesheetScale")
    val homeScale by animateFloatAsState(if (isHomeClicked) 1.1f else 1f, label = "HomeScale")
    val attendanceScale by animateFloatAsState(if (isAttendanceClicked) 1.1f else 1f, label = "AttendanceScale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF10161F))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationItem(
            navController,
            R.drawable.timesheet,
            "TimesheetScreen",
            isTimesheetClicked,
            timesheetScale
        ) { isTimesheetClicked = it }

        NavigationItem(
            navController,
            R.drawable.home,
            "ProfileAnalyticsScreen",
            isHomeClicked,
            homeScale
        ) { isHomeClicked = it }

        NavigationItem(
            navController,
            R.drawable.attendance,
            "AttendanceScreen",
            isAttendanceClicked,
            attendanceScale
        ) { isAttendanceClicked = it }
    }
}

@Composable
fun NavigationItem(
    navController: NavController,
    iconRes: Int,
    destination: String,
    isClicked: Boolean,
    scale: Float,
    onClick: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onClick(true)
                    navController.navigate(destination)
                    coroutineScope.launch {
                        delay(300)
                        onClick(false)
                    }
                }
                .scale(scale)
        )
        if (currentRoute == destination) {
            Image(
                painter = painterResource(id = R.drawable.officialreddot),
                contentDescription = "Selected",
                modifier = Modifier.size(8.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0XFFFF4A4A))
            )
        }
    }
}
