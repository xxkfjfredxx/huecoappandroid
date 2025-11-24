package com.fredrueda.huecoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fredrueda.huecoapp.ui.navigation.AppNavGraph
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

        // Configuración de la ventana para que el contenido se dibuje detrás de las barras del sistema
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Controlador para personalizar la apariencia de las barras del sistema
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false // Iconos oscuros en la barra de estado
        controller.isAppearanceLightNavigationBars = false // Iconos oscuros en la barra de navegación

        // Captura del deep link para restablecer contraseña (uid + token)
        // Formato esperado: huecoapp://reset-password?uid=123&token=abc
        val intentData = intent?.data
        val uid = intentData?.getQueryParameter("uid") // ID del usuario
        val token = intentData?.getQueryParameter("token") // Token de verificación

        setContent {
            // Tema de la aplicación (Material Design 3)
            MyApplicationTheme {
                // Grafo de navegación de la app
                AppNavGraph(
                    // Si hay deep link, inicia en reset_password, sino en splash
                    startDestination = if (uid != null && token != null) "reset_password" else "splash",
                    uid = uid, // Parámetro uid para reseteo de contraseña
                    token = token // Parámetro token para reseteo de contraseña
                )
            }
        }
    }
}