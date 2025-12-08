package com.fredrueda.huecoapp.feature.map.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

@Composable
fun HuecoOverlayCard(
    hueco: HuecoResponse,
    onClose: () -> Unit,
    onPositivo: () -> Unit,
    onNegativo: () -> Unit,
    onReparado: () -> Unit,
    onAbierto: () -> Unit,
    onCerrado: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(260.dp)
            .wrapContentHeight()
            .shadow(18.dp, RoundedCornerShape(16.dp))
    ) {
        Box(Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Column(Modifier.padding(16.dp)) {

            // Imagen
            AsyncImage(
                model = hueco.imagen,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = hueco.descripcion ?: "Hueco #${hueco.id}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(8.dp))

            when (hueco.estado) {
                "pendiente_validacion" -> {
                    if (hueco.validadoUsuario == true) {
                        // 🔒 Ya validó este usuario → sin botones
                        val faltan = hueco.faltanValidaciones ?: 0
                        Text(
                            text = buildString {
                                append("Gracias por validar este hueco 🙌\n")
                                if (faltan > 0) {
                                    append("Faltan $faltan personas para confirmar este hueco.")
                                } else {
                                    append("Este hueco está casi listo para cambiar de estado.")
                                }
                            }
                        )
                    } else {
                        // ✅ Aún no ha votado → mostrar botones
                        Button(
                            onClick = onPositivo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Sí existe") }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = onNegativo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("No existe") }
                    }
                }

                "activo" -> {
                    Text("Hueco activo. ¿Estado actual?")

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onReparado,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Reparado") }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onAbierto,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sigue abierto") }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onCerrado,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cerrado") }
                }

                else -> {
                    // otros estados, si quieres solo mostrar texto
                    Text("Estado: ${hueco.estado}")
                }
            }
        }
    }
}
