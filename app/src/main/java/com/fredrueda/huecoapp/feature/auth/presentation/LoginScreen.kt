package com.fredrueda.huecoapp.feature.auth.presentation

import android.app.Activity
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fredrueda.huecoapp.R
import com.fredrueda.huecoapp.ui.components.ModernButton
import com.fredrueda.huecoapp.ui.components.ModernInput
import kotlinx.coroutines.delay

@Preview(showSystemUi = true, device = "id:pixel_6_pro")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
/**
 * Pantalla de inicio de sesión.
 * Muestra carrusel informativo, formulario de login y botones de login social.
 * Navega a Home tras autenticación exitosa.
 */
@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onAuthSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleSignIn = rememberGoogleCredentialSignIn(
        activity = activity,
        webClientId = "795059173963-b952gr3hp9chqkn3p9cmg1dv2e06g1h6.apps.googleusercontent.com"
    ) { success, idToken ->
        if (success) {
            viewModel.loginWithGoogle("$idToken")
        } else {
            Log.e("LoginScreen", "Error al iniciar sesión: $idToken")
        }
    }

    // Integración de Facebook Login: obtiene accessToken y lo envía al ViewModel
    val facebookSignIn = rememberFacebookSignIn(activity) { success, token, message ->
        if (success && token != null) {
            viewModel.loginWithFacebook(token)
        } else {
            Log.e("LoginScreen", "Facebook login falló: ${message ?: "Sin mensaje"}")
            Toast.makeText(context, message ?: "Error en Facebook Login", Toast.LENGTH_SHORT).show()
        }
    }

    val items = listOf(
        Triple(R.drawable.login_foto, "Reporta huecos fácilmente", "Toma una foto y ayuda a mejorar tu ciudad."),
        Triple(R.drawable.login_busqueda, "Mira el mapa en tiempo real", "Encuentra zonas críticas en tu comunidad."),
        Triple(R.drawable.login_comunidad, "Únete a la comunidad", "Colabora con otras personas.")
    )

    val infiniteCount = Int.MAX_VALUE
    val startPage = infiniteCount / 2 - (infiniteCount / 2) % items.size
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { infiniteCount })

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

    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
            onAuthSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val pagerHeight = screenHeight * 0.35f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        ModernInput(
            value = email,
            label = "Correo electrónico",
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it }
        )

        ModernInput(
            value = password,
            label = "Contraseña",
            isPassword = true,
            onValueChange = { password = it },
            isLast = true
        )

        TextButton(
            onClick = { onForgotPasswordClick() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Olvido su contraseña?", fontSize = 14.sp)
        }

        ModernButton(
            text = "Iniciar sesión",
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(email.trim(), password.trim())
                } else {
                    Log.e("LoginScreen", "Campos vacíos")
                }
            }
        )

        TextButton(
            onClick = { onRegisterClick() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Crear cuenta", fontSize = 14.sp, color = Color.Blue)
        }

        Text("ó inicia sesión con", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            SocialButton(icon = R.drawable.google, text = "Google", onClick = googleSignIn)
            SocialButton(icon = R.drawable.facebook, text = "Facebook", onClick = facebookSignIn)
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

/**
 * Item del carrusel superior.
 * @param image recurso de imagen
 * @param title título
 * @param description descripción
 */
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

/**
 * Botón de login social genérico.
 * @param icon recurso del ícono
 * @param text texto del botón
 * @param onClick acción de click
 */
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
