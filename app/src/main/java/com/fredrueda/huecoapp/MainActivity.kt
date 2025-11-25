package com.fredrueda.huecoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
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
    private var latestIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        latestIntent.value = intent

        setContent {
            MyApplicationTheme {
                AppNavGraph(
                    startDestination = Destinations.Splash.route,
                    intent = latestIntent.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        latestIntent.value = intent
        // La recomposición hará que el NavHost maneje el deep link
    }
}