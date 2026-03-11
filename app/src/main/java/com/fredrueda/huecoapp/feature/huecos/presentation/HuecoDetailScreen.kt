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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import coil.compose.AsyncImage
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

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
    hueco: HuecoResponse,
    onBackClick: () -> Unit,
    onSeeComments: () -> Unit,
    viewModel: HuecoDetailViewModel = hiltViewModel()
) {
    val comentarios = viewModel.comentarios.collectAsState().value
    val huecoDetail = viewModel.huecoDetail.collectAsState().value ?: hueco

    // Inicializar ViewModel con el hueco pasado y sincronizar
    LaunchedEffect(hueco.id) {
        viewModel.initializeWith(hueco)
        // Refrescamos SOLO los comentarios (es ligero) para ver si hay novedades de otros usuarios
        viewModel.loadComentarios(hueco.id)
        
        // El detalle completo lo pedimos solo si es estrictamente necesario (falta info)
        val needsDetailFetch = hueco.totalComentarios == null
        if (needsDetailFetch) {
            viewModel.loadHuecoDetail(hueco.id)
        }
    }

    // Lambda local que carga los comentarios y luego navega a la pantalla de comentarios
    val seeComments = {
        viewModel.loadComentarios(hueco.id)
        onSeeComments()
    }

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
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Contenido principal desplazable
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            HeaderImageSection(huecoDetail, viewModel)

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Info principal
                HuecoInfoSection(huecoDetail)
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                MiniMapSection(huecoDetail)
                Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
                Spacer(modifier = Modifier.height(24.dp))

                // Mostrar información de depuración si hay datos relevantes (miConfirmacion o faltanValidaciones)
                if (huecoDetail.miConfirmacion != null || huecoDetail.faltanValidaciones != null) {
                    Text(
                        text = "DEBUG: validadoUsuario=${huecoDetail.validadoUsuario} faltan=${huecoDetail.faltanValidaciones}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Normalizar estado y decidir UI
                val estadoInternal = mapEstadoValueToInternal(huecoDetail.estado)
                val needsValidation = estadoInternal == "pendiente_validacion" && (huecoDetail.validadoUsuario != true)
                if (needsValidation) {
                    Text("Ayuda a validar. Faltan ${huecoDetail.faltanValidaciones ?: 0} personas")
                    Spacer(modifier = Modifier.height(12.dp))
                    val selected = viewModel.selectedValidation.collectAsState().value
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.validarHuecoExiste(huecoDetail.id) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (selected == 1) Color(0xFFE0E0E0) else Color.White)
                        ) {
                            Text("Sí existe", color = Color.Black)
                        }
                        Button(
                            onClick = { viewModel.validarHuecoNoExiste(huecoDetail.id) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (selected == 0) Color(0xFFE0E0E0) else Color.White)
                        ) {
                            Text("No existe", color = Color.Black)
                        }
                    }
                } else {
                    // Si ya validó o no está pendiente, mostrar mensaje de agradecimiento cuando aplicable
                    val showed = (huecoDetail.validadoUsuario == true)
                    if (estadoInternal == "pendiente_validacion" && showed) {
                        Text("Ya validaste este hueco. Gracias 🙌", color = Color(0xFF4CAF50))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Spacer(modifier = Modifier.height(16.dp))
                ReporterSection(huecoDetail)
                Spacer(modifier = Modifier.height(24.dp))
                
                val comentariosCount = viewModel.comentariosCount.collectAsState().value
                // Sincronizar el conteo: Priorizar lo que diga el ViewModel que está más "fresco"
                CommentsSection(comentarios, comentariosCount = comentariosCount, onSeeComments = seeComments)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sección inferior: mostrar "¿Conoces el estado actual?" solo cuando el hueco esté ACTIVO
            if (mapEstadoValueToInternal(huecoDetail.estado) == "activo") {
                val isConfirming = viewModel.isConfirming.collectAsState().value
                val selectedConf = viewModel.selectedConfirmation.collectAsState().value
                BottomStatusUpdateSection(
                    onReparado = { viewModel.confirmarEstado(huecoDetail.id, 7) },
                    onEnReparacion = { viewModel.confirmarEstado(huecoDetail.id, 6) },
                    onCerrado = { viewModel.confirmarEstado(huecoDetail.id, 5) },
                    isDisabled = isConfirming,
                    showReabrir = false,
                    selectedConfirmation = selectedConf
                )
            }
            // Si el hueco está cerrado, permitir reabrir
            if (mapEstadoValueToInternal(huecoDetail.estado) == "cerrado") {
                val isConfirming = viewModel.isConfirming.collectAsState().value
                val selectedConf = viewModel.selectedConfirmation.collectAsState().value
                BottomStatusUpdateSection(
                    onReparado = { /* no-op */ },
                    onEnReparacion = { /* no-op */ },
                    onCerrado = { /* no-op */ },
                    onReabrir = { viewModel.confirmarEstado(huecoDetail.id, 4) },
                    isDisabled = isConfirming,
                    showReabrir = true,
                    selectedConfirmation = selectedConf
                )
            }
        }
    }
}

// 1. Sección de la Imagen de Cabecera con Estado y Favorito
@Composable
fun HeaderImageSection(hueco: HuecoResponse, viewModel: HuecoDetailViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
    ) {
        // Imagen real del hueco
        AsyncImage(
            model = hueco.imagen,
            contentDescription = "Imagen del hueco",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        )

        // Icono de favorito (corazón)
        IconButton(
            onClick = { viewModel.toggleFollow(hueco.id, hueco.isFollowed == true) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = if (hueco.isFollowed == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (hueco.isFollowed == true) "Dejar de seguir" else "Seguir",
                tint = if (hueco.isFollowed == true) HuecoRed else HuecoTextGray
            )
        }

        // Chip de Estado (normalizamos y mostramos etiqueta legible)
        val estadoLabel = mapEstadoValueToLabel(hueco.estado)
        val estadoInternal = mapEstadoValueToInternal(hueco.estado)
        Surface(
            color = when (estadoInternal) {
                "activo" -> HuecoRed
                "cerrado" -> HuecoGreen
                "en_reparacion" -> HuecoOrangeScore
                "reparado" -> HuecoGreen
                else -> HuecoYellow
            },
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
                    text = "Estado: $estadoLabel",
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
fun HuecoInfoSection(hueco: HuecoResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Columna izquierda: Título y Ubicación
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hueco #${hueco.id}",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
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
                    text = hueco.ciudad ?: "Sin dirección",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hueco.descripcion ?: "Sin descripción",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Columna derecha: Puntuación de Gravedad
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp) // Aumentado para que "Medio" quepa bien
                    .border(2.dp, HuecoOrangeScore.copy(alpha = 0.5f), CircleShape)
                    .background(HuecoOrangeScore.copy(alpha = 0.1f), CircleShape)
                    .padding(4.dp)
            ) {
                Text(
                    text = hueco.gravedad?.replaceFirstChar { it.uppercase() } ?: "-",
                    color = HuecoOrangeScore,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp // Un poco más pequeña para asegurar que no se corte
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "GRAVEDAD",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. Sección del Reportador
@Composable
fun ReporterSection(hueco: HuecoResponse) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF6495ED), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                (hueco.usuarioNombre?.take(2)?.uppercase() ?: "--"),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Reportado por ${hueco.usuarioNombre ?: "-"}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            val fechaReportShort = formatDateShort(hueco.fechaReporte)
            Text(
                text = "${if (fechaReportShort.isNotEmpty()) fechaReportShort else "-"} • ${hueco.vistas ?: 0} visualizaciones",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// 4. Sección del Mini Mapa
@Composable
fun MiniMapSection(hueco: HuecoResponse) {
    if (hueco.latitud == null || hueco.longitud == null) return

    Column {
        Text(
            text = "Ubicación Geográfica",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                var mapView by remember { mutableStateOf<org.osmdroid.views.MapView?>(null) }
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                            Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                            else -> {}
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                        mapView?.onDetach()
                    }
                }

                androidx.compose.ui.viewinterop.AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        org.osmdroid.views.MapView(ctx).apply {
                            setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(17.5)
                            val point = org.osmdroid.util.GeoPoint(hueco.latitud!!, hueco.longitud!!)
                            controller.setCenter(point)
                            
                            val marker = org.osmdroid.views.overlay.Marker(this)
                            marker.position = point
                            marker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM)
                            marker.icon = androidx.core.content.ContextCompat.getDrawable(ctx, org.osmdroid.library.R.drawable.marker_default)
                            marker.title = "Hueco #${hueco.id}"
                            overlays.add(marker)
                            mapView = this
                        }
                    },
                    update = { view ->
                        val point = org.osmdroid.util.GeoPoint(hueco.latitud!!, hueco.longitud!!)
                        view.controller.setCenter(point)
                    }
                )
            }
        }
    }
}

// 5. Sección de Comentarios
@Composable
fun CommentsSection(comentarios: List<ComentarioResponse>, comentariosCount: Int? = null, onSeeComments: () -> Unit) {
    Column {
        // Cabecera de comentarios
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayCount = comentariosCount ?: comentarios.size
            // Mostrar el conteo real si el ViewModel lo tiene
            Text(
                text = "Comentarios ($displayCount)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onSeeComments) {
                Text("Ver todos", color = HuecoOrangeScore)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Mostrar hasta 3 comentarios (los que llegan desde el map)
        val toShow = comentarios.take(3)
        toShow.forEach { c ->
            CommentItem(
                authorName = c.usuarioNombre ?: "",
                timeAgo = formatDateShort(c.fecha),
                content = c.texto ?: ""
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
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
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// 6. Sección Inferior de Actualización de Estado
@Composable
fun BottomStatusUpdateSection(
    onReparado: () -> Unit = {},
    onEnReparacion: () -> Unit = {},
    onCerrado: () -> Unit = {},
    onReabrir: () -> Unit = {},
    isDisabled: Boolean = false,
    showReabrir: Boolean = false,
    selectedConfirmation: Int? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.surface)
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
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Fila de botones Reparado / En Reparación / Cerrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val reparadoSelected = selectedConfirmation == 7
            val enReparacionSelected = selectedConfirmation == 6
            StatusActionButton(
                text = "Reparado",
                icon = Icons.Default.Build,
                backgroundColor = if (reparadoSelected) Color(0xFFE0E0E0) else HuecoYellow,
                modifier = Modifier.weight(1f),
                onClick = onReparado,
                enabled = !isDisabled
            )

            StatusActionButton(
                text = "En Reparación",
                icon = Icons.Default.Build,
                backgroundColor = if (enReparacionSelected) Color(0xFFE0E0E0) else HuecoOrangeScore,
                modifier = Modifier.weight(1f),
                onClick = onEnReparacion,
                enabled = !isDisabled
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón "Cerrado" como full-width
        val cerradoSelected = selectedConfirmation == 5
        Button(
            onClick = onCerrado,
            colors = ButtonDefaults.buttonColors(containerColor = if (cerradoSelected) Color(0xFFE0E0E0) else HuecoGreen),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isDisabled
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrado",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (showReabrir) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onReabrir, enabled = !isDisabled) {
                Text("Reabrir")
            }
        }
    }
}

@Composable
fun StatusActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(56.dp),
        enabled = enabled
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
    val hueco = HuecoResponse(
        id = 1,
        usuario = 2,
        usuarioNombre = "fredruedadeveloper",
        ciudad = "Armenia",
        descripcion = "Ejemplo de hueco para preview",
        latitud = 4.5336,
        longitud = -75.7061,
        estado = "activo",
        fechaReporte = "2025-11-27T23:50:21.629370Z",
        fechaActualizacion = "2025-12-24T02:09:49.328494Z",
        numeroCiclos = 0,
        validacionesPositivas = 0,
        validacionesNegativas = 1,
        gravedad = "media",
        vistas = 3,
        imagen = null,
        comentarios = listOf(
            ComentarioResponse(1, 2, "usuario1", "Buen reporte", null, "2025-12-27T12:00:00Z")
        ),
        totalComentarios = 1,
        confirmacionesCount = 0,
        validadoUsuario = false,
        miConfirmacion = null,
        faltanValidaciones = 0,
        isFollowed = false
    )
    HuecoDetailScreen(hueco = hueco, onBackClick = {}, onSeeComments = {})
}