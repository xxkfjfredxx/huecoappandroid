package com.fredrueda.huecoapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fredrueda.huecoapp.feature.auth.presentation.LoginScreen
import com.fredrueda.huecoapp.feature.auth.presentation.ResetPasswordScreen
import com.fredrueda.huecoapp.feature.home.presentation.MainHomeScreen
import com.fredrueda.huecoapp.feature.report.presentation.ReportScreen
import com.fredrueda.huecoapp.session.SessionViewModel
import com.fredrueda.huecoapp.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

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

    // 🔹 SessionViewModel que observa el estado de sesión
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isSessionActive by sessionViewModel.isSessionActive.collectAsState()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    LaunchedEffect(isSessionActive, currentRoute) {
        // Evita reaccionar mientras estás en el Splash
        if (currentRoute != "splash" && isSessionActive == false) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

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
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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

        // 📍 REPORT
        composable("report") {
            ReportScreen(onBack = { navController.popBackStack() })
        }

        // 🏠 HOME
        composable("home") {
            MainHomeScreen(
                onLogout = {
                    sessionViewModel.logout() // ✅ borra tokens → dispara redirección automática
                },
                onNavigateToMap = {
                    navController.navigate("report")
                }
            )
        }

        // 👤 PROFILE
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
