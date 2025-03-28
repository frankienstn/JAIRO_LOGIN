package com.example.jairosofttimesheet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jairosofttimesheet.data.remote.ApiService
import com.example.jairosofttimesheet.data.repository.Repository
import com.example.jairosofttimesheet.ui.screens.AttendanceScreen
import com.example.jairosofttimesheet.ui.screens.LoginScreen
import com.example.jairosofttimesheet.ui.screens.NavigationScreen
import com.example.jairosofttimesheet.ui.screens.ProfileAnalyticsScreen
import com.example.jairosofttimesheet.ui.screens.StartUpScreen
import com.example.jairosofttimesheet.ui.screens.TimesheetScreen
import com.example.jairosofttimesheet.ui.theme.JairosoftTimesheetTheme
import com.example.jairosofttimesheet.viewmodel.AttendanceViewModel
import com.example.jairosofttimesheet.viewmodel.ProfileViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JairosoftTimesheetTheme {
                val navController = rememberAnimatedNavController()

                // Create Repository instance
                val apiService = Retrofit.Builder()
                    .baseUrl("https://timesheet-63231.bubbleapps.io/api/1.1/wf/") // TODO: Replace with your actual base URL
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)

                val repository = Repository(apiService)

                val attendanceViewModel: AttendanceViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            AttendanceViewModel(repository, SavedStateHandle())
                        }
                    }
                )

                val profileViewModel: ProfileViewModel = viewModel()

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "StartUpScreen",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("StartUpScreen") {
                            StartUpScreen(navController)
                        }
                        composable("LoginScreen") {
                            LoginScreen(navController)
                        }
                        composable("NavigationScreen") {
                            NavigationScreen(
                                navController = navController,
                                attendanceViewModel = attendanceViewModel,
                                profileViewModel = profileViewModel
                            ) {
                                ProfileAnalyticsScreen(
                                    navController = navController,
                                    attendanceViewModel = attendanceViewModel,
                                    profileViewModel = profileViewModel
                                )
                            }
                        }
                        composable("ProfileAnalyticsScreen") {
                            NavigationScreen(
                                navController = navController,
                                attendanceViewModel = attendanceViewModel,
                                profileViewModel = profileViewModel
                            ) {
                                ProfileAnalyticsScreen(
                                    navController = navController,
                                    attendanceViewModel = attendanceViewModel,
                                    profileViewModel = profileViewModel
                                )
                            }
                        }
                        composable("AttendanceScreen") {
                            NavigationScreen(
                                navController = navController,
                                attendanceViewModel = attendanceViewModel,
                                profileViewModel = profileViewModel
                            ) {
                                AttendanceScreen(attendanceViewModel)
                            }
                        }
                        composable("TimesheetScreen") {
                            NavigationScreen(
                                navController = navController,
                                attendanceViewModel = attendanceViewModel,
                                profileViewModel = profileViewModel
                            ) {
                                TimesheetScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
