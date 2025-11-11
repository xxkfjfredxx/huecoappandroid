package com.fredrueda.huecoapp.feature.map.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Preview(showBackground = true)
@Composable
fun MapScreen( modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // 🔹 Solicitud de permisos en tiempo de ejecución
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
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

    Box(modifier = modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()

        // In Compose Preview, the map will not be displayed and a placeholder will be shown.
        // This is to prevent render errors caused by the MapView initialization.
        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Map preview not available")
            }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // The osmdroid configuration is initialized before the MapView is created.
                    Configuration.getInstance().load(
                        ctx,
                        ctx.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
                    )
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        isTilesScaledToDpi = true

                        // 🔸 Rotación del mapa habilitada
                        val rotationOverlay = RotationGestureOverlay(this)
                        rotationOverlay.isEnabled = true
                        overlays.add(rotationOverlay)

                        // 🔸 Brújula
                        val compassOverlay = CompassOverlay(ctx, this)
                        compassOverlay.enableCompass()
                        overlays.add(compassOverlay)

                        controller.setZoom(18.0)
                        //controller.setCenter(GeoPoint(4.6097, -74.0817))

                        mapView = this
                    }
                },
                update = { view ->
                    if (hasLocationPermission) {
                        scope.launch {
                            enableMyLocation(context, mapView = view)
                        }
                    }
                }
            )
        }


        // 🔹 Botón flotante "Mi ubicación"
        FloatingActionButton(
            onClick = {
                mapView?.let { map ->
                    val overlay = map.overlays.find { it is MyLocationNewOverlay } as? MyLocationNewOverlay
                    val loc = overlay?.myLocation
                    if (loc != null) {
                        map.controller.animateTo(loc)
                    }
                }
            },
            containerColor = Color(0xFFFFD000),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding() // 👈 evita que se corte con la barra inferior
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación", tint = Color.Black)
        }
    }
}

@SuppressLint("MissingPermission")
private suspend fun enableMyLocation(context: Context, mapView: MapView) {
    withContext(Dispatchers.Main) {
        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            val provider = GpsMyLocationProvider(context)
            val locationOverlay = MyLocationNewOverlay(provider, mapView)
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()
            mapView.overlays.add(locationOverlay)

            // Centrar en la ubicación actual cuando esté disponible
            locationOverlay.runOnFirstFix {
                mapView.controller.setCenter(locationOverlay.myLocation)
            }
        }
    }
}
