package com.fredrueda.huecoapp.feature.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
        onReportarCerrado = { id -> viewModel.reportarCerrado(id) }
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

    // Variable global para guardar el último huecoId a reabrir
    var lastReopenHuecoId: Int? by remember { mutableStateOf(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
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

    Box(modifier = modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()

        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("El mapa no está disponible en la vista previa.") }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->

                    Configuration.getInstance().load(
                        ctx,
                        ctx.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
                    )

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

                    // ------- Redibujar marcadores ------- //
                    val markerMap = mutableMapOf<Int, Marker>() // NUEVO: Mapa de huecoId a Marker
                    if (state.huecos.isNotEmpty()) {

                        // limpiar marcadores anteriores (pero no overlays del sistema)
                        view.overlays.removeAll { it is Marker }

                        state.huecos.forEach { hueco ->
                            val lat = hueco.latitud ?: return@forEach
                            val lon = hueco.longitud ?: return@forEach

                            val marker = Marker(view).apply {
                                id = hueco.id.toString() // Asigna el id del marker
                                position = GeoPoint(lat, lon)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = hueco.descripcion ?: "Hueco #${hueco.id}"
                                icon = ContextCompat.getDrawable(context, R.drawable.ic_huecoapp)

                                // ---- INFO WINDOW ---- //
                                infoWindow = HuecoInfoWindow(
                                    context = context,
                                    mapView = view,
                                    onClosed = { cerrarOverlay() },
                                ) {
                                    HuecoOverlayCard(
                                        hueco = hueco,
                                        onClose = { closeInfoWindow() },
                                        onToggleSeguir = { onValidarHuecoExiste(hueco.id) },
                                        onVerDetalle = {
                                            closeInfoWindow()
                                            onNavigateToDetail(hueco)
                                        },
                                        onValidarSiExiste = { onValidarHuecoExiste(hueco.id) },
                                        onValidarNoExiste = { onValidarHuecoNoExiste(hueco.id) },
                                        onReparado = { onReportarReparado(hueco.id) },
                                        onAbierto = { onReportarAbierto(hueco.id) },
                                        onCerrado = { onReportarCerrado(hueco.id) }
                                    )
                                }

                                setOnMarkerClickListener { m, _ ->
                                    // Cierra cualquier popup abierto antes de abrir este
                                    InfoWindow.closeAllInfoWindowsOn(view)
                                    seleccionarHueco(hueco)
                                    m.showInfoWindow()
                                    true
                                }
                            }
                            markerMap[hueco.id] = marker // Guardar referencia
                            view.overlays.add(marker)
                        }

                        view.invalidate()
                    }

                    // Cerrar InfoWindow si la bandera se activa tras votar
                    if (state.closeInfoWindow) {
                        mapView?.let { InfoWindow.closeAllInfoWindowsOn(it) }
                        // Guardar el huecoId a reabrir
                        lastReopenHuecoId = state.reopenInfoWindowId
                    }
                    viewModel.infoWindowCerrado()

                    // Forzar "parpadeo" visual: reabrir InfoWindow solo del marker correcto
                    lastReopenHuecoId?.let { huecoId ->
                        mapView?.let { map ->
                            val marker = map.overlays.filterIsInstance<Marker>().find {
                                it.id == huecoId.toString()
                            }
                            if (marker != null) {
                                Log.d("MapScreen", "Reabriendo InfoWindow para marker id=$huecoId")
                                InfoWindow.closeAllInfoWindowsOn(map)
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    marker.showInfoWindow()
                                    map.controller.animateTo(marker.position)
                                    lastReopenHuecoId = null
                                    viewModel.limpiarReopenInfoWindow()
                                }, 350)
                            } else {
                                Log.w("MapScreen", "No se encontró marker para id=$huecoId al intentar reabrir InfoWindow")
                                lastReopenHuecoId = null
                                viewModel.limpiarReopenInfoWindow()
                            }
                        }
                    }
                }
            )

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
}

@Composable
private fun HuecoOverlayCard(
    hueco: HuecoResponse,
    onClose: () -> Unit,
    onToggleSeguir: () -> Unit,
    onVerDetalle: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    // ... Código existente ...

    // Ejemplo de cómo se podría manejar el evento de cerrar el InfoWindow
    // en el botón de cerrar de la tarjeta del hueco
    IconButton(
        onClick = {
            onClose()
            viewModel.infoWindowCerrado() // <-- AÑADIDO
        },
        modifier = Modifier // Elimino .align(Alignment.TopEnd) para evitar error
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar"
        )
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
