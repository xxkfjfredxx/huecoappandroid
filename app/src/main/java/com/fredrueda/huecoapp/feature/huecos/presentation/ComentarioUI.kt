package com.fredrueda.huecoapp.feature.huecos.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de datos sugerido para los comentarios
data class ComentarioUI(
    val id: Int,
    val autor: String,
    val fecha: String,
    val contenido: String,
    val iniciales: String,
    val colorAvatar: Color = Color(0xFF6495ED)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    comentarios: List<ComentarioUI>, // Pasa tu lista aquí
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Comentarios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(bottom = 1.dp) // Sutil separación
            )
        },
        containerColor = Color(0xFFF8F8F8) // Un gris muy claro de fondo para resaltar los cards blancos
    ) { paddingValues ->

        if (comentarios.isEmpty()) {
            // Estado vacío opcional
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay comentarios aún.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cards
            ) {
                items(comentarios) { comentario ->
                    CommentCardItem(comentario)
                }
            }
        }
    }
}

@Composable
fun CommentCardItem(comentario: ComentarioUI) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar con iniciales
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(comentario.colorAvatar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comentario.iniciales,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comentario.autor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = comentario.fecha,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = comentario.contenido,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF444444)
                )
            }
        }
    }
}

// Extensión para añadir una sombra sutil al top bar (opcional)
fun Modifier.shadow(bottom: androidx.compose.ui.unit.Dp) = this.then(
    Modifier.padding(bottom = bottom).background(Color.Black.copy(alpha = 0.05f))
)

// Previews para visualizar los componentes
@Preview(showBackground = true)
@Composable
fun ComentariosScreenPreview() {
    val comentarios = listOf(
        ComentarioUI(
            id = 1,
            autor = "Juan Pérez",
            fecha = "Hace 2 horas",
            contenido = "Este es un comentario de ejemplo para probar la vista previa del componente.",
            iniciales = "JP",
            colorAvatar = Color(0xFF6495ED)
        ),
        ComentarioUI(
            id = 2,
            autor = "María López",
            fecha = "Ayer",
            contenido = "Otro comentario de ejemplo con más texto para ver cómo se comporta el diseño con contenido más largo.",
            iniciales = "ML",
            colorAvatar = Color(0xFFE74C3C)
        ),
        ComentarioUI(
            id = 3,
            autor = "Carlos García",
            fecha = "Hace 1 día",
            contenido = "Comentario más corto.",
            iniciales = "CG",
            colorAvatar = Color(0xFF2ECC71)
        )
    )
    
    ComentariosScreen(comentarios = comentarios, onBackClick = { })
}

@Preview(showBackground = true)
@Composable
fun CommentCardItemPreview() {
    val comentario = ComentarioUI(
        id = 1,
        autor = "Usuario de Prueba",
        fecha = "Ahora",
        contenido = "Este es un comentario de ejemplo para la vista previa del card individual.",
        iniciales = "UP",
        colorAvatar = Color(0xFF6495ED)
    )
    
    Surface {
        CommentCardItem(comentario)
    }
}