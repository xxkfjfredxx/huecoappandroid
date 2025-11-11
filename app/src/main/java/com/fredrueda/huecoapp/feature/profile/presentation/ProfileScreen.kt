package com.fredrueda.huecoapp.feature.profile.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredrueda.huecoapp.R

@Preview(showBackground = true)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    // 🔹 Fondo base + scroll + padding gestionado por Scaffold
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState()) // ✅ evita cortes en pantallas pequeñas
            .padding(horizontal = 24.dp)
            .systemBarsPadding(), // ✅ protege de notch y status bar
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 👤 Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.logo_huecoapp),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🧾 Nombre y nivel
        Text(
            text = "Fred Rueda",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Ciudadano nivel 3 ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text("⭐", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 🟨 Tarjeta de estadísticas
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
                Text("Puntos acumulados: 240", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Nivel: Confiable", color = Color.DarkGray)
                Text("Reportes enviados: 12", color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 🟡 Botón “Editar perfil”
        Button(
            onClick = { /* TODO editar perfil */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD000)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Editar perfil",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
