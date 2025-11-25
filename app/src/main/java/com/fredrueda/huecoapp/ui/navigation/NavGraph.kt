package com.fredrueda.huecoapp.ui.navigation

import android.content.Intent
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
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.fredrueda.huecoapp.feature.auth.presentation.ForgotPasswordScreen
import com.fredrueda.huecoapp.feature.auth.presentation.LoginScreen
import com.fredrueda.huecoapp.feature.auth.presentation.RegisterScreen
import com.fredrueda.huecoapp.feature.auth.presentation.ResetPasswordScreen
import com.fredrueda.huecoapp.feature.auth.presentation.VerifyRegisterScreen
import com.fredrueda.huecoapp.feature.home.presentation.MainHomeScreen
import com.fredrueda.huecoapp.feature.report.presentation.ReportScreen
import com.fredrueda.huecoapp.session.SessionViewModel
import com.fredrueda.huecoapp.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    startDestination: String = Destinations.Splash.route,
    uid: String? = null,
    token: String? = null,
    intent: Intent? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Session
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isSessionActive by sessionViewModel.isSessionActive.collectAsState()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // 🔹 Detectar si hay un deeplink de reset-password
    val hasDeepLink = intent?.data?.scheme == "huecoapp" &&
                     intent.data?.host == "reset-password"

    LaunchedEffect(intent) {
        if (intent?.action == Intent.ACTION_VIEW) {
            navController.handleDeepLink(intent)
        }
    }

    LaunchedEffect(isSessionActive, currentRoute) {
        if (currentRoute != Destinations.Splash.route &&
            !currentRoute.orEmpty().startsWith("reset-password") &&
            isSessionActive == false) {
            navController.navigate(Destinations.Login.route) {
                popUpTo(Destinations.Home.route) { inclusive = true }
            }
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(700)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(700)
            )
        }
    ) {

        composable(Destinations.Splash.route) {
            SplashScreen(
                navController = navController,
                hasDeepLink = hasDeepLink
            )
        }

        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onAuthSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Destinations.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                }
            )
        }

        composable(Destinations.Register.route) {
            RegisterScreen(navController)
        }

        composable(
            route = Destinations.VerifyRegister.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")!!
            VerifyRegisterScreen(navController, email)
        }

        composable(
            route = "reset-password?uid={uid}&token={token}",
            arguments = listOf(
                navArgument("uid") { defaultValue = "" },
                navArgument("token") { defaultValue = "" }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "huecoapp://reset-password?uid={uid}&token={token}"
                }
            )
        ) { backStackEntry ->
            val uidArg = backStackEntry.arguments?.getString("uid") ?: ""
            val tokenArg = backStackEntry.arguments?.getString("token") ?: ""

            ResetPasswordScreen(
                uid = uidArg,
                token = tokenArg,
                onSuccess = {
                    sessionViewModel.logout()
                    navController.popBackStack(Destinations.Splash.route, false)
                },
                onBack = {
                    navController.popBackStack(Destinations.Splash.route, false)
                }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("report") {
            ReportScreen(onBack = { navController.popBackStack() })
        }

        composable(Destinations.Home.route) {
            MainHomeScreen(
                onLogout = { sessionViewModel.logout() },
                onNavigateToMap = {
                    navController.navigate("report")
                }
            )
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
