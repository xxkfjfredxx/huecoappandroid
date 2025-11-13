package com.fredrueda.huecoapp.ui.navigation

sealed class Destinations(val route: String) {
    object Splash : Destinations("splash")
    object Login : Destinations("login")
    object Home : Destinations("home")

    // 👇 NUEVOS
    object Register : Destinations("register")
    object VerifyRegister : Destinations("verify_register?email={email}")
}
