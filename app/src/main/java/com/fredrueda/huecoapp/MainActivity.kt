package com.fredrueda.huecoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fredrueda.huecoapp.ui.navigation.AppNavGraph
import com.fredrueda.huecoapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        // Activa edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Control de colores de barra (por ahora negro)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        setContent {
            MyApplicationTheme {
                AppNavGraph()
            }
        }
    }
}
