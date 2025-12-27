package com.fredrueda.huecoapp.feature.huecos.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definición de colores personalizados basados en el diseño
val HuecoYellow = Color(0xFFFFC107)
val HuecoYellowLight = Color(0xFFFFF59D) // Para el botón grande inferior
val HuecoRed = Color(0xFFD32F2F)
val HuecoGreen = Color(0xFF388E3C)
val HuecoOrangeScore = Color(0xFFFF9800)
val HuecoTextGray = Color(0xFF757575)
val HuecoBackgroundGray = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuecoDetailScreen(
    huecoId: Int,
    onBackClick: () -> Unit,
    onSeeComments: () -> Unit)
{
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalle del Hueco",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle share click */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Contenido principal desplazable
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            HeaderImageSection()

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HuecoInfoSection()
                Divider(color = HuecoBackgroundGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                ReporterSection()
                Divider(color = HuecoBackgroundGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                MiniMapSection()
                Spacer(modifier = Modifier.height(24.dp))
                CommentsSection(onSeeComments = onSeeComments)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sección inferior (parece un bottom sheet persistente)
            BottomStatusUpdateSection()
        }
    }
}

// 1. Sección de la Imagen de Cabecera con Estado y Favorito
@Composable
fun HeaderImageSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        // Placeholder para la imagen principal. Reemplazar con AsyncImage (Coil)
        Image(
            painter = painterResource(android.R.drawable.ic_menu_gallery), // REEMPLAZAR IMAGEN REAL
            contentDescription = "Imagen del hueco",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        )

        // Icono de favorito (corazón)
        IconButton(
            onClick = { /* TODO: Toggle favorito */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorito",
                tint = HuecoTextGray
            )
        }

        // Chip de Estado (Activo)
        Surface(
            color = HuecoRed,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estado: Activo",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 2. Sección de Información Principal (Título, Ubicación, Gravedad)
@Composable
fun HuecoInfoSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Columna izquierda: Título y Ubicación
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Huecote #4829",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = HuecoYellow,
                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Av. Principal con Calle 5, Esquina Norte",
                    color = HuecoTextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Columna derecha: Puntuación de Gravedad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .border(2.dp, HuecoOrangeScore.copy(alpha = 0.5f), CircleShape)
                    .background(HuecoOrangeScore.copy(alpha = 0.1f), CircleShape)
            ) {
                Text(
                    text = "4.5",
                    color = HuecoOrangeScore,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "GRAVEDAD",
                fontSize = 10.sp,
                color = HuecoTextGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. Sección del Reportador
@Composable
fun ReporterSection() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Placeholder para avatar. Reemplazar con imagen real.
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF6495ED), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("JD", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Reportado por Juan D.",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Hace 2 horas • 12 visualizaciones",
                fontSize = 12.sp,
                color = HuecoTextGray
            )
        }
    }
}

// 4. Sección del Mini Mapa
@Composable
fun MiniMapSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder para el mapa. Usar GoogleMap o OSMDroid aquí.
            Image(
                painter = painterResource(android.R.drawable.ic_dialog_map), // REEMPLAZAR CON MAPA REAL
                contentDescription = "Mapa pequeño",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(Color(0xFFDCA795)) // Color de fondo temporal similar a la imagen
            )

            // Marcador central
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.align(Alignment.Center).size(32.dp)
            )

            // Botón "Ver Mapa Completo"
            Button(
                onClick = { /* TODO: Abrir mapa grande */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Text(
                    "Ver Mapa Completo",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 5. Sección de Comentarios
@Composable
fun CommentsSection(onSeeComments: () -> Unit) {
    Column {
        // Cabecera de comentarios
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comentarios (3)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onSeeComments) {
                Text("Ver todos", color = HuecoOrangeScore)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Ítem de comentario individual
        CommentItem(
            authorName = "Maria Gonzalez",
            timeAgo = "Hace 15 min",
            content = "Es horrible, casi se me pincha la llanta pasando por ahí. ¡Necesita arreglo urgente!"
        )
    }
}

@Composable
fun CommentItem(authorName: String, timeAgo: String, content: String) {
    // CAMBIO: verticalAlignment en lugar de crossAxisAlignment
    Row(verticalAlignment = Alignment.Top) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(HuecoBackgroundGray, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = authorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = HuecoTextGray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray
            )
        }
    }
}

// 6. Sección Inferior de Actualización de Estado
@Composable
fun BottomStatusUpdateSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pequeña barra de "arrastre" (visual)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(HuecoBackgroundGray, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Conoces el estado actual?",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Fila de botones Reparado / Cerrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusActionButton(
                text = "Reparado",
                icon = Icons.Default.Build,
                backgroundColor = HuecoYellow,
                modifier = Modifier.weight(1f)
            ) { /* TODO: Acción Reparado */ }

            StatusActionButton(
                text = "Cerrado",
                icon = Icons.Default.Check,
                backgroundColor = HuecoGreen,
                modifier = Modifier.weight(1f)
            ) { /* TODO: Acción Cerrado */ }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón grande de Reportar Actualización
        Button(
            onClick = { /* TODO: Reportar otra cosa */ },
            colors = ButtonDefaults.buttonColors(containerColor = HuecoYellowLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reportar Actualización",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun StatusActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(56.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}


@Preview(showBackground = true, heightDp = 900)
@Composable
fun HuecoDetailScreenPreview() {
    MaterialTheme {
        HuecoDetailScreen(1, {}, {})
    }
}