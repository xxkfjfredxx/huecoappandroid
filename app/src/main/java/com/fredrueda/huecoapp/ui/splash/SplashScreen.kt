package com.fredrueda.huecoapp.ui.splash

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.fredrueda.huecoapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview(showSystemUi = true, device = "id:pixel_6_pro")
@Composable
fun SplashScreen(navController: NavController) {
    val view = LocalView.current
    val window = (view.context as Activity).window

    LaunchedEffect(Unit) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)

        // ✅ Ocultar barras SOLO en Splash
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Tu animación / delay
        delay(2000)

        // ✅ Mostrar barras otra vez ANTES de ir a Login
        controller.show(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )

        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD600))
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- SVG Logo ---
            Image(
                painter = painterResource(id = R.drawable.logo_huecoapp),
                contentDescription = "Logo HuecoApp",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Texto animado ---
            CrashTextAnimation(text = "HuecoApp")
        }
    }

}

@Composable
fun CrashTextAnimation(text: String) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(-500f) }
    val rotations = remember { text.map { Animatable(0f) } }
    val yOffsets = remember { text.map { Animatable(0f) } }

    LaunchedEffect(Unit) {
        // Movimiento del texto hasta el centro
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1200, easing = LinearOutSlowInEasing)
        )

        // Animación de "colisión"
        rotations.forEachIndexed { i, rot ->
            scope.launch {
                rot.animateTo(
                    targetValue = (-30..30).random().toFloat(),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            }
        }

        // Letras caen o se desacomodan un poco
        yOffsets.forEachIndexed { i, offset ->
            scope.launch {
                offset.animateTo(
                    targetValue = (-10..10).random().toFloat(),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.toInt(), 0) },
        horizontalArrangement = Arrangement.Center
    ) {
        text.forEachIndexed { i, c ->
            Text(
                text = c.toString(),
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                modifier = Modifier
                    .graphicsLayer(
                        rotationZ = rotations[i].value,
                        translationY = yOffsets[i].value
                    )
                    .padding(horizontal = 2.dp)
            )
        }
    }
}
