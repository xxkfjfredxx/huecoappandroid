package com.fredrueda.huecoapp.ui.splash

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.fredrueda.huecoapp.R
import com.fredrueda.huecoapp.session.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavController,
    hasDeepLink: Boolean = false
) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    val context = view.context
    val session = remember { SessionManager(context) }
    var animationEnded by remember { mutableStateOf(false) }

    // 🔹 Ocultar barras del sistema SOLO en el Splash
    LaunchedEffect(Unit) {
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // 🔹 Cuando termina la animación, mostramos barras y navegamos
    LaunchedEffect(animationEnded) {
        if (animationEnded) {
            controller.show(
                WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
            )

            delay(400) // 🔸 pequeño delay para suavizar la transición

            // 🔹 Solo bloquear navegación en cold start por deeplink
            val isDeepLinkInBackStack = try {
                navController.getBackStackEntry("reset-password?uid={uid}&token={token}")
                true
            } catch (_: Exception) { false }
            val shouldHoldForDeepLink = hasDeepLink && navController.previousBackStackEntry == null && isDeepLinkInBackStack
            if (shouldHoldForDeepLink) {
                return@LaunchedEffect
            }

            // 🔹 Flujo normal: verificar sesión
            val access = session.getAccess()

            if (!access.isNullOrBlank()) {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // 🔹 Pantalla visual
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
            CrashTextAnimation(
                text = "HuecoApp",
                onAnimationEnd = { animationEnded = true } // 🔹 sincroniza navegación
            )
        }
    }
}

@Composable
fun CrashTextAnimation(
    text: String,
    onAnimationEnd: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(-500f) }
    val rotations = remember { text.map { Animatable(0f) } }
    val yOffsets = remember { text.map { Animatable(0f) } }

    LaunchedEffect(Unit) {
        // 🔹 Animación de entrada
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1200, easing = LinearOutSlowInEasing)
        )

        // 🔹 Animaciones simultáneas (rebote de letras)
        rotations.forEachIndexed { _, rot ->
            scope.launch {
                rot.animateTo(
                    targetValue = (-30..30).random().toFloat(),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            }
        }

        yOffsets.forEachIndexed { _, offset ->
            scope.launch {
                offset.animateTo(
                    targetValue = (-10..10).random().toFloat(),
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                )
            }
        }

        delay(1200)
        onAnimationEnd() // 🔹 notifica al Splash que terminó
    }

    Row(
        modifier = Modifier.offset { IntOffset(offsetX.value.toInt(), 0) },
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
