package com.fredrueda.huecoapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fredrueda.huecoapp.feature.auth.presentation.LoginScreen
import com.fredrueda.huecoapp.feature.auth.presentation.ResetPasswordScreen
import com.fredrueda.huecoapp.feature.auth.presentation.google.handleGoogleSignIn
import com.fredrueda.huecoapp.feature.home.presentation.MainHomeScreen
import com.fredrueda.huecoapp.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    startDestination: String = "splash",
    uid: String? = null,
    token: String? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(700))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(700))
        }
    ) {
        // 🟡 SPLASH
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // 🟢 LOGIN
        composable("login") {
            LoginScreen(
                onLoginClick = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoogleLogin = {
                    scope.launch {
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

        // 🔑 RESET PASSWORD
        composable("reset_password") {
            ResetPasswordScreen(
                uid = uid ?: "",
                token = token ?: "",
                onSuccess = {
                    navController.navigate("login") {
                        popUpTo("reset_password") { inclusive = true }
                    }
                }
            )
        }

        // 🏠 HOME con Drawer
        composable("home") {
            MainHomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    // 🔹 Navega al mapa global o al de creación de reporte
                    navController.navigate("map")
                }
            )
        }

        composable("map") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pantalla del mapa", color = Color.Gray)
            }
        }

        composable("profile") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Perfil del usuario", color = Color.Gray)
            }
        }

    }
}
