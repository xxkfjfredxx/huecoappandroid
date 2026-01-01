package com.fredrueda.huecoapp.feature.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fredrueda.huecoapp.feature.huecos.presentation.mapEstadoValueToInternal
import com.fredrueda.huecoapp.feature.huecos.presentation.mapEstadoValueToLabel
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

// Clase para proveer datos de ejemplo al preview
class HuecoResponseProvider : PreviewParameterProvider<HuecoResponse> {
    override val values = sequenceOf(
        HuecoResponse(
            id = 1,
            usuario = 1,
            usuarioNombre = "Usuario Ejemplo",
            descripcion = "Hueco de ejemplo",
            latitud = 4.6097,
            longitud = -74.0817,
            imagen = "",
            estado = "pendiente_validacion",
            isFollowed = false,
            fechaReporte = "2023-01-01",
            fechaActualizacion = "2023-01-01",
            comentarios = emptyList(),
            confirmacionesCount = 0,
            validacionesPositivas = 0,
            validacionesNegativas = 0,
            validadoUsuario = false,
            miConfirmacion = null,
            faltanValidaciones = 0,
            numeroCiclos = 0,
            gravedad = "Normal",
            vistas = 0,
            totalComentarios = 1,
            ciudad = "Bogotá",
        )
    )
}

@Preview(showSystemUi = true, device = "id:pixel_6_pro")
@Composable
fun HuecoOverlayCardPreview(
    @PreviewParameter(HuecoResponseProvider::class) hueco: HuecoResponse
) {
    HuecoOverlayCard(
        hueco = hueco,
        onClose = {},
        onToggleSeguir = {},
        onVerDetalle = {},
        onValidarSiExiste = {},
        onValidarNoExiste = {},
        onReparado = {},
        onAbierto = {},
        onCerrado = {}
    )
}

@Composable
fun HuecoOverlayCard(
    hueco: HuecoResponse,
    onClose: () -> Unit,
    onToggleSeguir: () -> Unit,
    onVerDetalle: () -> Unit,
    onValidarSiExiste: () -> Unit,
    onValidarNoExiste: () -> Unit,
    onReparado: () -> Unit,
    onAbierto: () -> Unit,
    onCerrado: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .width(320.dp)
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // --- PARTE SUPERIOR: INFO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = hueco.imagen,
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hueco.descripcion ?: "Hueco #${hueco.id}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Ajuste de nombres según tu archivo HuecoOverlayCard.kt
                    val total = hueco.totalComentarios ?: hueco.comentarios?.size ?: 0
                    Text(
                        text = "${total} comentarios",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Estado :",
                        fontSize = 13.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    StatusChip(estadoRaw = hueco.estado)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Black)
                    }

                    Spacer(Modifier.height(8.dp))

                    IconButton(onClick = onToggleSeguir, modifier = Modifier.size(32.dp)) {
                        // es_seguido debe estar en tu HuecoResponse
                        Icon(
                            imageVector = if (hueco.isFollowed == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Seguir",
                            tint = if (hueco.isFollowed == true) Color(0xFFFFC107) else Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Botón principal
            Button(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6E971)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ir al detalle Hueco", color = Color.Black, fontWeight = FontWeight.Black)
            }

            // --- SECCIÓN CONDICIONAL ---
            // --- SECCIÓN CONDICIONAL ---
            val estadoInternal = mapEstadoValueToInternal(hueco.estado)
            when (estadoInternal) {
                "pendiente_validacion" -> {
                    ValidationSection(
                        hueco = hueco,
                        onPositivo = onValidarSiExiste,
                        onNegativo = onValidarNoExiste
                    )
                }
                "activo", "reabierto" -> {
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                    Text(
                        "¿ Conoces el estado actual ?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Determinar si el hueco tiene una confirmación del usuario y marcar el botón correspondiente
                        val selectedConf = hueco.miConfirmacion?.nuevoEstado
                        val reparadoSelected = selectedConf == 7
                        val enReparacionSelected = selectedConf == 6
                        val cerradoSelected = selectedConf == 5

                        SmallStateButton(
                            "Reparado",
                            if (reparadoSelected) Color(0xFFE0E0E0) else Color(0xFF4CAF50),
                            Modifier.weight(1f),
                            onReparado
                        )

                        SmallStateButton(
                            "En Reparación",
                            if (enReparacionSelected) Color(0xFFE0E0E0) else Color(0xFFFF9800),
                            Modifier.weight(1f),
                            onAbierto
                        )

                        SmallStateButton(
                            "Cerrado",
                            if (cerradoSelected) Color(0xFFE0E0E0) else Color(0xFF2196F3),
                            Modifier.weight(1f),
                            onCerrado
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ValidationSection(hueco: HuecoResponse, onPositivo: () -> Unit, onNegativo: () -> Unit) {
    HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

    // Aquí envolvemos todo en un Column con horizontalAlignment para que el texto se centre correctamente
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar mensaje si ya validó o si el voto es true (validación positiva)
        if (hueco.validadoUsuario == true || hueco.miConfirmacion?.voto == true) {
            Text(
                "Ya validaste este hueco. Gracias 🙌",
                fontSize = 13.sp,
                color = Color(0xFF4CAF50)
            )
        } else {
            Text(
                "Ayuda a validar. Faltan ${hueco.faltanValidaciones ?: 0} personas",
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onPositivo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Sí existe", fontSize = 12.sp) }

                OutlinedButton(
                    onClick = onNegativo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("No existe", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun SmallStateButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun StatusChip(estadoRaw: Any?) {
    val estadoInternal = mapEstadoValueToInternal(estadoRaw)
    val text = mapEstadoValueToLabel(estadoRaw)
    val color = when (estadoInternal) {
        "pendiente_validacion" -> Color(0xFFFFC107) // amarillo
        "activo" -> Color(0xFFD32F2F) // rojo (activo - visible como advertencia)
        "reabierto" -> Color(0xFFFFA000) // naranja
        "cerrado" -> Color(0xFF4CAF50) // verde
        "en_reparacion" -> Color(0xFFFF9800) // naranja intenso
        "reparado" -> Color(0xFF4CAF50) // verde
        "rechazado" -> Color(0xFF9E9E9E) // gris
        else -> Color.Gray
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}