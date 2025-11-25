package com.fredrueda.huecoapp.feature.auth.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredrueda.huecoapp.ui.components.ModernButton
import kotlinx.coroutines.launch

/**
 * Pantalla de restablecimiento de contraseña via deep link.
 * Recibe uid y token del enlace y llama al backend.
 */
@Composable
fun ResetPasswordScreen(
    uid: String,
    token: String,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.resetPasswordState

    if (state.isSuccess) {
        LaunchedEffect(true) {
            Toast.makeText(
                context,
                state.message ?: "Contraseña actualizada correctamente",
                Toast.LENGTH_LONG
            ).show()

            onSuccess()
        }
    }
    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Restablecer contraseña", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        ModernButton(
            text = "Cambiar contraseña",
            onClick = {
                if (password == confirm) {
                    scope.launch {
                        viewModel.resetPassword(
                            uid = uid,
                            token = token,
                            newPassword = password
                        )
                    }
                } else {
                    message = "Las contraseñas no coinciden"
                }
            }
        )

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

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}