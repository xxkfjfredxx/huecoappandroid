package com.fredrueda.huecoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.fredrueda.huecoapp.ui.navigation.AppNavGraph
import com.fredrueda.huecoapp.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        // 🟡 Captura del deep link (uid + token)
        val intentData = intent?.data
        val uid = intentData?.getQueryParameter("uid")
        val token = intentData?.getQueryParameter("token")

        setContent {
            MyApplicationTheme {
                AppNavGraph(
                    startDestination = if (uid != null && token != null) "reset_password" else "splash",
                    uid = uid,
                    token = token
                )
            }
        }
    }
}