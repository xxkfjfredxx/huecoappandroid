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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import kotlinx.coroutines.launch

// Modelo de datos sugerido para los comentarios (UI)
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
    huecoId: Int,
    onBackClick: () -> Unit,
    viewModel: HuecoDetailViewModel = hiltViewModel()
) {
    // Estado del ViewModel
    val comentarios by viewModel.comentarios.collectAsState()

    // Cargar comentarios al abrir la pantalla (solo la primera vez o cuando cambie huecoId)
    LaunchedEffect(huecoId) {
        viewModel.resetComentarios()
        viewModel.loadComentarios(huecoId)
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Campo de texto para nuevo comentario
    var newText by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    // Cuando la lista cambia, hacer scroll al inicio (más reciente arriba)
    LaunchedEffect(comentarios.size) {
        if (comentarios.isNotEmpty()) {
            scope.launch {
                // Los comentarios están ordenados: más recientes primero, por eso ir al índice 0
                listState.animateScrollToItem(0)
            }
        }
    }

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
                modifier = Modifier
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (comentarios.isEmpty()) {
                // Estado vacío opcional, dejar espacio para la barra inferior
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay comentarios aún.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comentarios) { comentario ->
                        CommentCardItem(comentario.toUI())
                    }
                }
            }

            // Barra inferior para escribir y enviar comentario (fondo blanco)
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newText,
                        onValueChange = { newText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un comentario...", color = Color.Gray) },
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                         val texto = newText.trim()
                         if (texto.isNotEmpty()) {
                             viewModel.postComentario(huecoId, texto, null)
                             newText = ""
                             // limpiar foco (esto también oculta el teclado)
                             focusManager.clearFocus()
                         }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HuecoYellow)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }
}

// Extensión para mapear ComentarioResponse -> ComentarioUI
fun ComentarioResponse.toUI(): ComentarioUI {
    val nombre = this.usuarioNombre ?: "Usuario"
    val iniciales = nombre.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
    return ComentarioUI(
        id = this.id,
        autor = nombre,
        fecha = formatDateShort(this.fecha),
        contenido = this.texto ?: "",
        iniciales = iniciales.uppercase()
    )
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

// Previews
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
        )
    )

    ComentariosScreen(huecoId = 1, onBackClick = { })
}