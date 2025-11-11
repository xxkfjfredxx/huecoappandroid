package com.fredrueda.huecoapp.feature.auth.presentation

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredrueda.huecoapp.R
import kotlinx.coroutines.delay

@Preview(showSystemUi = true, device = "id:pixel_6_pro")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onGoogleLogin: () -> Unit = {},
    onFacebookLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity

    val googleSignIn = rememberGoogleCredentialSignIn(
        activity = activity,
        webClientId = "795059173963-b952gr3hp9chqkn3p9cmg1dv2e06g1h6.apps.googleusercontent.com"
    ) { success, message ->
        if (success) {
            onLoginClick() // navega al home
        } else {
            Log.e("LoginScreen", "Error al iniciar sesión: $message")
        }
    }

    // --- Configuración del carrusel infinito ---
    val items = listOf(
        Triple(R.drawable.login_foto, "Reporta huecos fácilmente", "Toma una foto y ayuda a mejorar tu ciudad."),
        Triple(R.drawable.login_busqueda, "Mira el mapa en tiempo real", "Encuentra zonas críticas en tu comunidad."),
        Triple(R.drawable.login_comunidad, "Únete a la comunidad", "Colabora con otras personas.")
    )

    val infiniteCount = Int.MAX_VALUE
    val startPage = infiniteCount / 2 - (infiniteCount / 2) % items.size
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { infiniteCount })

    // --- Autoplay estable ---
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            if (!pagerState.isScrollInProgress) {
                val next = pagerState.currentPage + 1
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                )
            }
        }
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val pagerHeight = screenHeight * 0.35f // 35% del alto total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ---------- 🔹 CARRUSEL SUPERIOR ----------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = pagerHeight)
        ) { page ->
            val realIndex = page % items.size
            val (image, title, desc) = items[realIndex]
            PagerItem(image = image, title = title, description = desc)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔸 Indicadores dinámicos
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(items.size) { index ->
                val realIndex = pagerState.currentPage % items.size
                val color = if (realIndex == index) Color(0xFFFFD600) else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (realIndex == index) 12.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- 🔹 FORMULARIO LOGIN ----------
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Text("Forgot Password?", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Crear cuenta", fontSize = 14.sp,color = Color.Blue)
        }


        Text("ó inicia sesión con", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialButton(icon = R.drawable.google, text = "Google", onClick = googleSignIn)
            SocialButton(icon = R.drawable.facebook, text = "Facebook", onClick = onFacebookLogin)
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Al ingresar, aceptas los términos y condiciones de uso de la aplicación.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PagerItem(image: Int, title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.sizeIn(minHeight = 150.dp, maxHeight = 200.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
fun SocialButton(icon: Int, text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
