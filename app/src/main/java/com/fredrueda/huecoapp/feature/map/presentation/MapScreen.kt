package com.fredrueda.huecoapp.feature.map.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fredrueda.huecoapp.R
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToDetail: (HuecoResponse) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    MapScreenContent(
        modifier = modifier,
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        limpiarMensaje = { viewModel.limpiarMensaje() },
        cargarHuecosCercanos = { lat, lon -> viewModel.cargarHuecosCercanos(lat, lon) },
        cerrarOverlay = { viewModel.cerrarOverlay() },
        seleccionarHueco = { hueco -> viewModel.seleccionarHueco(hueco) },
        onValidarHuecoExiste = { id -> viewModel.validarHuecoExiste(id) },
        onValidarHuecoNoExiste = { id -> viewModel.validarHuecoNoExiste(id) },
        onReportarReparado = { id -> viewModel.reportarReparado(id) },
        onReportarAbierto = { id -> viewModel.reportarAbierto(id) },
        onReportarCerrado = { id -> viewModel.reportarCerrado(id) },
        viewModel = viewModel
    )
}

@Composable
private fun MapScreenContent(
    modifier: Modifier = Modifier,
    state: MapUiState,
    onNavigateToDetail: (HuecoResponse) -> Unit,
    limpiarMensaje: () -> Unit,
    cargarHuecosCercanos: (Double, Double) -> Unit,
    cerrarOverlay: () -> Unit,
    seleccionarHueco: (HuecoResponse) -> Unit,
    onValidarHuecoExiste: (Int) -> Unit,
    onValidarHuecoNoExiste: (Int) -> Unit,
    onReportarReparado: (Int) -> Unit,
    onReportarAbierto: (Int) -> Unit,
    onReportarCerrado: (Int) -> Unit,
    viewModel: MapViewModel = androidx.hilt.navigation.compose.hiltViewModel() // <-- AÑADIDO
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var locationInitialized by remember { mutableStateOf(false) }



    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

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

    if (!LocalInspectionMode.current) {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            limpiarMensaje()
        }
    }

    // Cerrar el overlay automáticamente cuando salimos de la pantalla del mapa
    DisposableEffect(Unit) {
        onDispose {
            cerrarOverlay()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val scope = rememberCoroutineScope()

        // --- SECCIÓN: DIALOGO DE DENUNCIA ---
        var showDenunciaDialog by remember { mutableStateOf(false) }

        LaunchedEffect(state.reportSuccess) {
            if (state.reportSuccess) {
                Toast.makeText(context, "Gracias por reportar. El contenido será revisado.", Toast.LENGTH_LONG).show()
                viewModel.resetReportState()
                showDenunciaDialog = false
            }
        }

        LaunchedEffect(state.reportError) {
            state.reportError?.let { err ->
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                viewModel.resetReportState()
            }
        }

        if (showDenunciaDialog && state.selectedHueco != null) {
            com.fredrueda.huecoapp.feature.huecos.presentation.DenunciaDialog(
                onDismiss = { showDenunciaDialog = false },
                onConfirm = { motivo, comentario ->
                    viewModel.reportarHueco(state.selectedHueco.id, motivo, comentario)
                }
            )
        }

        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("El mapa no está disponible en la vista previa.") }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->

                    val config = Configuration.getInstance()
                    config.load(ctx, ctx.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
                    
                    // Mejoras de Caché para OSMDroid
                    config.userAgentValue = ctx.packageName
                    val tileCacheDir = java.io.File(ctx.cacheDir, "osmdroid/tiles")
                    config.osmdroidTileCache = tileCacheDir
                    config.tileFileSystemCacheMaxBytes = 250L * 1024 * 1024 // 250 MB
                    config.tileFileSystemCacheTrimBytes = 200L * 1024 * 1024 // 200 MB

                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        isTilesScaledToDpi = true

                        overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
                        overlays.add(CompassOverlay(ctx, this).apply { enableCompass() })

                        controller.setZoom(18.0)

                        mapView = this
                    }
                },
                update = { view ->

                    // ------- Inicializar ubicación ------- //
                    if (hasLocationPermission && !locationInitialized) {
                        locationInitialized = true

                        scope.launch {
                            enableMyLocation(context, view) { lat, lon ->
                                cargarHuecosCercanos(lat, lon)
                            }
                        }
                    }

                    // ------- Cerrar InfoWindow al tocar el mapa ------- //
                    if (view.overlays.none { it is MapTouchOverlay }) {
                        view.overlays.add(MapTouchOverlay {
                            InfoWindow.closeAllInfoWindowsOn(view)
                            cerrarOverlay()
                        })
                    }

                    // ------- Redibujar marcadores con CLUSTERING ------- //
                    val markerMap = mutableMapOf<Int, Marker>()
                    if (state.huecos.isNotEmpty()) {

                        // limpiar marcadores anteriores y clusters anteriores
                        view.overlays.removeAll { it is Marker || it is org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer }

                        // Crear el gestor de agrupamiento de pines
                        val clusterer = object : org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer(context) {
                            init {
                                // Design personalizado para el cluster (Circulo con número)
                                val clusterColor = android.graphics.Color.rgb(255, 152, 0) // Naranja vibrante
                                val clusterSize = 120
                                val bitmap = Bitmap.createBitmap(clusterSize, clusterSize, Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(bitmap)
                                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                                // Dibujar borde blanco
                                paint.color = android.graphics.Color.WHITE
                                canvas.drawCircle(clusterSize / 2f, clusterSize / 2f, clusterSize / 2f, paint)

                                // Dibujar fondo naranja
                                paint.color = clusterColor
                                canvas.drawCircle(clusterSize / 2f, clusterSize / 2f, clusterSize / 2f * 0.88f, paint)

                                setIcon(bitmap)
                                // Centrar el texto en el círculo
                                mTextAnchorU = 0.5f
                                mTextAnchorV = 0.5f
                                
                                mTextPaint.apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 44f
                                    isFakeBoldText = true
                                    textAlign = Paint.Align.CENTER
                                }
                            }
                        }

                        state.huecos.forEach { hueco ->
                            val lat = hueco.latitud ?: return@forEach
                            val lon = hueco.longitud ?: return@forEach

                            val marker = Marker(view).apply {
                                id = hueco.id.toString()
                                position = GeoPoint(lat, lon)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = hueco.descripcion ?: "Hueco #${hueco.id}"
                                icon = ContextCompat.getDrawable(context, R.drawable.ic_huecoapp)

                                setOnMarkerClickListener { m, _ ->
                                    seleccionarHueco(hueco)
                                    view.controller.animateTo(m.position)
                                    true
                                }
                            }
                            markerMap[hueco.id] = marker // Guardar referencia
                            clusterer.add(marker) // NUEVO: Añadir al cluster, no a la vista directo
                        }
                        
                        // Añadir el cluster completo al mapa
                        view.overlays.add(clusterer)
                    }
                    view.invalidate()
                }
            )
        }

        // ------- Overlay de Hueco Seleccionado (Compose nativo) -------
        AnimatedVisibility(
            visible = state.selectedHueco != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            state.selectedHueco?.let { selected ->
                HuecoOverlayCard(
                    hueco = selected,
                    onClose = { cerrarOverlay() },
                    onToggleSeguir = { viewModel.toggleFollow(selected.id, selected.isFollowed == true) },
                    onVerDetalle = {
                        cerrarOverlay()
                        onNavigateToDetail(selected)
                    },
                    onValidarSiExiste = { onValidarHuecoExiste(selected.id) },
                    onValidarNoExiste = { onValidarHuecoNoExiste(selected.id) },
                    onReparado = { onReportarReparado(selected.id) },
                    onAbierto = { onReportarAbierto(selected.id) },
                    onCerrado = { onReportarCerrado(selected.id) },
                    onReportar = { showDenunciaDialog = true }
                )
            }
        }

        // ------- Botón de ubicación ------- //
        if (hasLocationPermission) {
            FloatingActionButton(
                onClick = {
                    mapView?.let { view ->
                        scope.launch {
                            enableMyLocation(context, view) { lat, lon ->
                                cargarHuecosCercanos(lat, lon)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                containerColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Ubicación",
                    tint = Color.Blue
                )
            }
        }
    }
}

private class MapTouchOverlay(
    private val onTouch: () -> Unit
) : Overlay() {
    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        // No dibujamos nada en el overlay
    }

    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
        onTouch()
        return true
    }
}

@SuppressLint("MissingPermission")
private suspend fun enableMyLocation(
    context: Context,
    mapView: MapView,
    onLocationReady: (Double, Double) -> Unit
) {
    withContext(Dispatchers.Main) {
        val provider = GpsMyLocationProvider(context)
        val overlay = MyLocationNewOverlay(provider, mapView)

        overlay.enableMyLocation()
        overlay.enableFollowLocation()
        mapView.overlays.add(overlay)

        overlay.runOnFirstFix {
            val loc = overlay.myLocation ?: return@runOnFirstFix
            mapView.post {
                mapView.controller.setCenter(loc)
            }
            onLocationReady(loc.latitude, loc.longitude)
        }
    }
}
