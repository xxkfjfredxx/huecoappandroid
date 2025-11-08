package com.fredrueda.huecoapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fredrueda.huecoapp.feature.auth.presentation.LoginScreen
import com.fredrueda.huecoapp.feature.auth.presentation.google.handleGoogleSignIn
import com.fredrueda.huecoapp.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(startDestination: String = "splash") {
    val navController = rememberNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(700)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(700)) }

    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            val context = LocalContext.current
            val scope = rememberCoroutineScope() // 👈 esto crea un alcance de corrutina Compose

            LoginScreen(
                onLoginClick = {
                    navController.navigate("home")
                },
                onGoogleLogin = {
                    scope.launch { // 👈 aquí lanzas la corrutina
                        handleGoogleSignIn(context) { success ->
                            if (success) {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                onFacebookLogin = { /* TODO */ }
            )
        }

        composable("home") {
            SplashScreen(navController)
        }

    }
}
