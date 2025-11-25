package com.fredrueda.huecoapp.feature.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredrueda.huecoapp.ui.components.ModernButton
import com.fredrueda.huecoapp.ui.components.ModernInput

/**
 * Pantalla para solicitar recuperación de contraseña.
 * Envía el email al endpoint /password/forgot/.
 */
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val state = viewModel.forgotPasswordState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        ModernInput(
            value = email,
            label = "Correo electrónico",
            onValueChange = { email = it },
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModernButton(
            text = "Enviar correo",
            loading = state.isLoading
        ) {
            if (email.isNotBlank()) {
                viewModel.forgotPassword(email.trim())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }

        state.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio de sesión")
        }
    }
}
