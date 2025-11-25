package com.fredrueda.huecoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fredrueda.huecoapp.ui.navigation.AppNavGraph
import com.fredrueda.huecoapp.ui.navigation.Destinations
import com.fredrueda.huecoapp.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de HuecoApp.
 * 
 * Esta actividad es el punto de entrada de la aplicación y gestiona:
 * - Configuración de la UI (barras de sistema, tema oscuro)
 * - Navegación inicial de la aplicación
 * - Procesamiento de deep links para restablecer contraseña
 * 
 * @author Fred Rueda
 * @version 1.0
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        setContent {
            MyApplicationTheme {
                AppNavGraph(
                    startDestination = Destinations.Splash.route
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // NO procesar deep link aquí.
    }
}