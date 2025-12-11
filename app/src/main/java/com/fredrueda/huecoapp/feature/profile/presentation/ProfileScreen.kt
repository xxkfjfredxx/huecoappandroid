package com.fredrueda.huecoapp.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fredrueda.huecoapp.R
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.entity.ReputacionDto
import com.fredrueda.huecoapp.feature.auth.domain.entity.StatsDto

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()

    ProfileContent(
        modifier = modifier,
        user = user,
        error = error
    )
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    user: AuthUser?,
    error: String?
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ⚠️ Error simple si algo falla
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 👤 Foto de perfil
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(user?.fotoPerfil ?: R.drawable.logo_huecoapp)
                .crossfade(true)
                .build(),
            placeholder = painterResource(id = R.drawable.logo_huecoapp),
            error = painterResource(id = R.drawable.logo_huecoapp),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🧾 Nombre / username
        val nombreVisible = when {
            !user?.firstName.isNullOrBlank() || !user?.lastName.isNullOrBlank() ->
                "${user?.firstName.orEmpty()} ${user?.lastName.orEmpty()}".trim()
            !user?.username.isNullOrBlank() -> user?.username.orEmpty()
            else -> "Usuario"
        }

        Text(
            text = nombreVisible,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Nivel de reputación
        val nivel = user?.reputacion?.nivel ?: "nuevo"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Ciudadano $nivel",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(" ⭐", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 🟨 Tarjeta de estadísticas principales
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF59D)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val puntosTotales = user?.puntosTotales ?: 0
                Text(
                    "Puntos acumulados: $puntosTotales",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                val nivelTexto = user?.reputacion?.nivel ?: "nuevo"
                Text("Nivel: ${nivelTexto.replaceFirstChar { it.uppercaseChar() }}", color = Color.DarkGray)

                val reportes = user?.stats?.reportes ?: 0
                Text("Reportes enviados: $reportes", color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🧮 Tarjeta secundaria con más stats
        val stats = user?.stats
        if (stats != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatRow(
                        label = "Huecos seguidos",
                        value = stats.huecosSeguidos ?: 0
                    )
                    StatRow(
                        label = "Validaciones realizadas",
                        value = stats.validacionesRealizadas ?: 0
                    )
                    StatRow(
                        label = "Confirmaciones realizadas",
                        value = stats.confirmacionesRealizadas ?: 0
                    )
                    StatRow(
                        label = "Comentarios realizados",
                        value = stats.comentariosRealizados ?: 0
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatRow(
    label: String,
    value: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value.toString(), color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// Preview solo de UI, con datos falsos
@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview() {
    ProfileContent(
        user = AuthUser(
            id = 1,
            username = "frueda",
            firstName = "Fred",
            lastName = "Rueda",
            email = "admin@admin.com",
            isActive = true,
            isStaff = true,
            isSuperuser = true,
            authProvider = "email",
            employeeId = "EMP-00001",
            puntosTotales = 10,
            detallePuntos = mapOf("reporte" to 10),
            reputacion = ReputacionDto(nivel = "nuevo", puntajeTotal = 10),
            stats = StatsDto(
                reportes = 1,
                huecosSeguidos = 0,
                validacionesRealizadas = 0,
                confirmacionesRealizadas = 0,
                comentariosRealizados = 0
            ),
            fotoPerfil = null
        ),
        error = null
    )
}
