package com.fredrueda.huecoapp.ui.navigation

/**
 * Destinos de navegación de la aplicación.
 * 
 * Define todas las rutas de navegación como sealed class para garantizar
 * type-safety y evitar errores de tipeo en las rutas.
 * 
 * Uso:
 * ```kotlin
 * navController.navigate(Destinations.Login.route)
 * ```
 * 
 * @property route Ruta de navegación (string)
 * @author Fred Rueda
 * @version 1.0
 */
sealed class Destinations(val route: String) {
    /** Pantalla de bienvenida / splash screen */
    object Splash : Destinations("splash")
    
    /** Pantalla de inicio de sesión */
    object Login : Destinations("login")
    
    /** Pantalla principal / dashboard */
    object Home : Destinations("home")

    object Profile : Destinations("profile")


    /** Pantalla de registro de nuevos usuarios */
    object Register : Destinations("register")
    
    /** Pantalla de verificación de código OTP (con parámetro email) */
    object VerifyRegister : Destinations("verify_register?email={email}")

    object DetalleHueco : Destinations("detalle_hueco/{huecoId}") {
        fun createRoute(huecoId: Int) = "detalle_hueco/$huecoId"
    }
    object Comentarios : Destinations("comentarios/{huecoId}") {
        fun createRoute(huecoId: Int) = "comentarios/$huecoId"
    }
}
