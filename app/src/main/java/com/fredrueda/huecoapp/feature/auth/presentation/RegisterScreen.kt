// RegisterScreen.kt and VerifyRegisterScreen.kt
// Combined file for user review before splitting.

package com.fredrueda.huecoapp.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController, vm: AuthViewModel = hiltViewModel()) {
    val state = vm.registerState

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") }
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                vm.register(email, password, username, firstName, lastName)
            },
            enabled = !state.isLoading
        ) {
            Text("Registrarme")
        }

        if (state.isSuccess) {
            Spacer(Modifier.height(16.dp))
            Text(text = state.message ?: "Código enviado")

            // Navegar a verificación
            LaunchedEffect(true) {
                navController.navigate("verify_register?email=$email")
            }
        }

        if (state.isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun VerifyRegisterScreen(navController: NavController, email: String, vm: AuthViewModel = hiltViewModel()) {
    val state = vm.verifyRegisterState
    val authState = vm.state.collectAsState().value

    var code by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verificar correo", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código de verificación") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                vm.verifyRegister(email, code)
            },
            enabled = !state.isLoading
        ) {
            Text("Verificar")
        }

        if (state.isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (state.isVerified && authState.user != null) {
            // Navegar al home o pantalla principal
            LaunchedEffect(true) {
                navController.navigate("home") {
                    popUpTo(0)
                }
            }
        }

        state.error?.let {
            Spacer(Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
