package com.example.jairosofttimesheet

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jairosofttimesheet.ui.screens.AttendanceScreen
import com.example.jairosofttimesheet.ui.screens.ForgotPasswordScreen
import com.example.jairosofttimesheet.ui.screens.LoginScreen
import com.example.jairosofttimesheet.ui.screens.NavigationScreen
import com.example.jairosofttimesheet.ui.screens.ProfileAnalyticsScreen
import com.example.jairosofttimesheet.ui.screens.StartUpScreen
import com.example.jairosofttimesheet.ui.screens.TimesheetScreen
import com.example.jairosofttimesheet.ui.theme.JairosoftTimesheetTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JairosoftTimesheetTheme {
                val navController = rememberAnimatedNavController()

                //for lesser delay when first navigating through Login -> PA.. backStackEntry
                val destination = navController.currentBackStackEntryAsState().value?.destination?.route
                key(destination) {
                    ProfileAnalyticsScreen(navController)
                }

                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "StartUpScreen",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("StartUpScreen",
                            exitTransition = {
                                fadeOut(
                                    animationSpec = tween(800) // 600ms fade-in duration
                                )
                            },
                            )
                        {
                            StartUpScreen(navController)
                        }
                        composable(
                            "LoginScreen",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(800) // 600ms fade-in duration
                                )
                            },
                            exitTransition = {
                                when (targetState.destination.route) {
                                    "NavigationScreen" -> slideOutVertically(
                                    targetOffsetY = { -it },
                                        animationSpec = tween(
                                            durationMillis = 800,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    else -> slideOutHorizontally(
                                        targetOffsetX = { -it },
                                        animationSpec = tween(600)
                                    )
                                }
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(600)
                                )
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(600)
                                )
                            }
                        ) {
                            LoginScreen(navController)
                        }

                        composable(
                            "ForgotPasswordScreen",
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(600)
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(400)
                                )
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(600)
                                )
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(600)
                                )
                            }
                        ) {
                            ForgotPasswordScreen(navController)
                        }

                        composable(
                            "NavigationScreen",
                            enterTransition = {
                                if (initialState.destination.route == "LoginScreen") {
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = tween(600)
                                    )
                                } else EnterTransition.None
                            },
                            exitTransition = {
                                if (targetState.destination.route == "LoginScreen") {
                                    slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = tween(600)
                                    )
                                } else ExitTransition.None
                            }
                        ) {
                            NavigationScreen(navController = navController) {
                                ProfileAnalyticsScreen(navController)
                            }
                        }

                        composable("ProfileAnalyticsScreen",
                            enterTransition = {
                                when (initialState.destination.route) {
                                    "AttendanceScreen" -> slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600)) // From Left
                                    "TimesheetScreen" -> slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600)) // From Right
                                    "LoginScreen" -> slideInVertically(initialOffsetY = { it }, animationSpec = tween(600)) // From Bottom
                                    else -> EnterTransition.None
                                }
                            },
                            exitTransition = {
                                when (targetState.destination.route) {
                                    "AttendanceScreen" -> slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600)) // To Left
                                    "TimesheetScreen" -> slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600))  // To Right
                                    "LoginScreen" -> slideOutVertically(targetOffsetY = { it }, animationSpec = tween(600)) // To Bottom
                                    else -> ExitTransition.None
                                }
                            }
                        ) {
                            NavigationScreen(navController = navController) {
                                ProfileAnalyticsScreen(navController)
                            }
                        }

                        composable("AttendanceScreen",
                            enterTransition = {
                                when (initialState.destination.route) {
                                    "ProfileAnalyticsScreen" -> slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600))
                                    "TimesheetScreen" -> slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(600))
                                    else -> EnterTransition.None
                                }
                            },
                            exitTransition = {
                                when (targetState.destination.route) {
                                    "ProfileAnalyticsScreen" -> slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600))
                                    "TimesheetScreen" -> slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600))
                                    else -> ExitTransition.None
                                }
                            }
                        ) {
                            NavigationScreen(navController = navController) {
                                AttendanceScreen()
                            }
                        }


                        composable("TimesheetScreen",
                            enterTransition = {
                                when (initialState.destination.route) {
                                    "ProfileAnalyticsScreen" -> slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600))
                                    "AttendanceScreen" -> slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(600))
                                    else -> EnterTransition.None
                                }
                            },
                            exitTransition = {
                                when (targetState.destination.route) {
                                    "ProfileAnalyticsScreen" -> slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(600))
                                    "AttendanceScreen" -> slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(600))
                                    else -> ExitTransition.None
                                }
                            }
                        ) {
                            NavigationScreen(navController = navController) {
                                TimesheetScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartUpScreenPreview() {
    JairosoftTimesheetTheme {
        val navController = rememberNavController()
        StartUpScreen(navController)
    }
}
