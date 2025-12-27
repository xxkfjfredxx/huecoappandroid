package com.fredrueda.huecoapp.feature.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.fredrueda.huecoapp.R
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

@Preview(showBackground = true)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
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

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()

        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Preview no disponible") }
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
                                viewModel.cargarHuecosCercanos(lat, lon)
                            }
                        }
                    }

                    // ------- Cerrar InfoWindow al tocar el mapa ------- //
                    if (view.overlays.none { it is MapTouchOverlay }) {
                        view.overlays.add(MapTouchOverlay {
                            InfoWindow.closeAllInfoWindowsOn(view)
                            viewModel.cerrarOverlay()
                        })
                    }

                    // ------- Redibujar marcadores ------- //
                    if (state.huecos.isNotEmpty()) {

                        // limpiar marcadores anteriores (pero no overlays del sistema)
                        view.overlays.removeAll { it is Marker }

                        state.huecos.forEach { hueco ->
                            val lat = hueco.latitud ?: return@forEach
                            val lon = hueco.longitud ?: return@forEach

                            val marker = Marker(view).apply {
                                position = GeoPoint(lat, lon)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = hueco.descripcion ?: "Hueco #${hueco.id}"
                                icon = ContextCompat.getDrawable(context, R.drawable.ic_huecoapp)

                                // ---- INFO WINDOW ---- //
                                infoWindow = HuecoInfoWindow(
                                    context = context,
                                    mapView = view,
                                    onClosed = { viewModel.cerrarOverlay() },
                                ) {
                                    HuecoOverlayCard(
                                        hueco = hueco,
                                        onClose = { closeInfoWindow() },
                                        onToggleSeguir = { viewModel.validarHuecoExiste(hueco.id) },
                                        onVerDetalle = {
                                            closeInfoWindow()
                                            onNavigateToDetail(hueco.id) },
                                        onValidarSiExiste = { viewModel.reportarReparado(hueco.id) },
                                        onValidarNoExiste = { viewModel.reportarReparado(hueco.id) },
                                        onReparado = { viewModel.reportarReparado(hueco.id) },
                                        onAbierto = { viewModel.reportarAbierto(hueco.id) },
                                        onCerrado = { viewModel.reportarCerrado(hueco.id) }
                                    )
                                }

                                setOnMarkerClickListener { m, _ ->
                                    // Cierra cualquier popup abierto antes de abrir este
                                    InfoWindow.closeAllInfoWindowsOn(view)
                                    viewModel.seleccionarHueco(hueco)
                                    m.showInfoWindow()
                                    true
                                }

                            }

                            view.overlays.add(marker)
                        }

                        view.invalidate()
                    }
                }
            )
        }

        // ------- FAB MI UBICACIÓN ------- //
        FloatingActionButton(
            onClick = {
                mapView?.let { map ->
                    val overlay =
                        map.overlays.find { it is MyLocationNewOverlay } as? MyLocationNewOverlay
                    overlay?.myLocation?.let { loc ->
                        map.controller.animateTo(loc)
                    }
                }
            },
            containerColor = Color(0xFFFFD000),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación", tint = Color.Black)
        }
    }
}

// -------- Overlay que detecta toque en el mapa -------- //
class MapTouchOverlay(private val onTap: () -> Unit) : Overlay() {
    override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
        onTap()
        return false
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
