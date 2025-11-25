package com.fredrueda.huecoapp.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fredrueda.huecoapp.ui.components.ModernButton
import com.fredrueda.huecoapp.ui.components.ModernInput
import org.json.JSONObject

/**
 * Pantalla de registro de usuario.
 * Construye formulario, valida contraseñas y dispara registro.
 * Navega a verificación al éxito.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: AuthViewModel = hiltViewModel()
) {
    val state = vm.registerState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()                      // Primero tamaño completo
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .imePadding()  // Luego scroll correcto
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Crear cuenta", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))

            ModernInput(
                value = email,
                label = "Correo",
                keyboardType = KeyboardType.Email
            ) { email = it }

            val passwordsMatch = password == confirmPassword && password.isNotEmpty()

            ModernInput(
                value = password,
                label = "Contraseña",
                isPassword = true,
                isError = confirmPassword.isNotEmpty() && !passwordsMatch
            ) {
                password = it
                passwordError = null
            }

            ModernInput(
                value = confirmPassword,
                label = "Confirmar contraseña",
                isPassword = true,
                isError = confirmPassword.isNotEmpty() && !passwordsMatch
            ) {
                confirmPassword = it
                passwordError = null
            }

            if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                Text(
                    "Las contraseñas no coinciden.",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            if (passwordError != null) {
                Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            ModernInput(
                value = username,
                label = "Nombre de usuario"
            ) { username = it }

            ModernInput(
                value = firstName,
                label = "Nombre"
            ) { firstName = it }

            ModernInput(
                value = lastName,
                label = "Apellido",
                isLast = true
            ) { lastName = it }

            Spacer(Modifier.height(24.dp))

            ModernButton(
                text = "Registrarme",
                loading = state.isLoading,
                onClick = {
                    if (password != confirmPassword) {
                        passwordError = "Las contraseñas no coinciden."
                        return@ModernButton
                    }

                    vm.register(email, password, username, firstName, lastName)
                }
            )

            if (state.isSuccess) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = state.message ?: "Código enviado",
                    color = MaterialTheme.colorScheme.primary
                )

                LaunchedEffect(true) {
                    navController.navigate("verify_register?email=$email")
                }
            }
        }
    }
}

/**
 * Pantalla de verificación de registro.
 * Solicita código OTP y al éxito navega a Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyRegisterScreen(
    navController: NavController,
    email: String,
    vm: AuthViewModel = hiltViewModel()
) {
    val state = vm.verifyRegisterState
    val authState = vm.state.collectAsState().value

    var code by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Verificar correo", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))

            ModernInput(
                value = code,
                label = "Código de verificación",
                keyboardType = KeyboardType.Number,
                isLast = true
            ) { code = it }

            Spacer(Modifier.height(28.dp))

            ModernButton(
                text = "Verificar",
                loading = state.isLoading,
                onClick = {
                    vm.verifyRegister(email, code)
                }
            )

            // ---- MOSTRAR SOLO EL TEXTO DEL ERROR ----
            state.error?.let { rawError ->

                val cleanError = try {
                    // Intentar extraer el campo "detail" si existe
                    val json = JSONObject(rawError)
                    if (json.has("detail")) json.getString("detail") else rawError
                } catch (_: Exception) {
                    // Si no es JSON, mostrar mensaje completo
                    rawError
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    cleanError,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // ---- NAVEGAR CUANDO VERIFICA ----
            if (state.isVerified && authState.user != null) {
                LaunchedEffect(true) {
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                }
            }
        }
    }
}
