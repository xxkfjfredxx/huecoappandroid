package com.fredrueda.huecoapp.feature.report.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.fredrueda.huecoapp.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun ReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // --- Estado UI
    var mapView: MapView? by remember { mutableStateOf(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // --- Pide permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasLocationPermission =
            perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        // Carga config de osmdroid
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )
        // Lanza permisos si no están dados
        val fineGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission = fineGranted || coarseGranted
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar hueco") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (userLocation == null) {
                        Toast.makeText(
                            context,
                            "Ubicación no detectada todavía",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showSheet = true
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black) },
                text = { Text("Reportar hueco aquí") },
                containerColor = Color(0xFFFFD000),
                contentColor = Color.Black,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    isClickable = true
                    isTilesScaledToDpi = true

                    // Rotación y gestos
                    overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })

                    // Brújula
                    overlays.add(
                        CompassOverlay(
                            ctx,
                            InternalCompassOrientationProvider(ctx),
                            this
                        ).apply { enableCompass() }
                    )
                    // Centro “fallback” mientras llega el primer fix
                    val fallback = GeoPoint(4.787316, -74.229235) // Bogotá (por decir)
                    controller.setZoom(17.0)
                    controller.setCenter(fallback)

                    // Marcador arrastrable (se reposiciona al tener ubicación real)
                    val marker = Marker(this).apply {
                        position = fallback
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Ubicación del hueco"
                        isDraggable = true
                        icon = ContextCompat.getDrawable(ctx, R.drawable.logo_huecoapp)
                        setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                            override fun onMarkerDrag(marker: Marker?) {}
                            override fun onMarkerDragEnd(marker: Marker?) {
                                marker?.let { userLocation = it.position }
                            }
                            override fun onMarkerDragStart(marker: Marker?) {}
                        })
                    }
                    overlays.add(marker)

                    // Capa de ubicación y “primer fix” -> centra y mueve el marcador ahí
                    if (hasLocationPermission) {
                        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()
                        overlays.add(locationOverlay)

                        locationOverlay.runOnFirstFix {
                            val loc: GeoPoint? = locationOverlay.myLocation
                            loc?.let { point ->
                                post {
                                    userLocation = point
                                    controller.setCenter(point)
                                    controller.setZoom(18.0)
                                    marker.position = point
                                    marker.title = "Tu ubicación actual"
                                    invalidate()
                                }
                            }
                        }
                    }

                    mapView = this
                }
            },
            update = { map ->
                mapView = map

                // Si concedieron permisos después de crear el mapa, activa ubicación y centra
                if (hasLocationPermission) {
                    val alreadyAdded =
                        map.overlays.any { it is MyLocationNewOverlay }
                    if (!alreadyAdded) {
                        val locationOverlay =
                            MyLocationNewOverlay(GpsMyLocationProvider(context), map)
                        locationOverlay.enableMyLocation()
                        locationOverlay.enableFollowLocation()
                        map.overlays.add(locationOverlay)

                        locationOverlay.runOnFirstFix {
                            val loc: GeoPoint? = locationOverlay.myLocation
                            loc?.let { point ->
                                map.post {
                                    userLocation = point
                                    map.controller.setCenter(point)
                                    map.controller.setZoom(18.0)
                                    // mueve el primer Marker encontrado
                                    map.overlays.filterIsInstance<Marker>()
                                        .firstOrNull()?.let { m ->
                                            m.position = point
                                        }
                                    map.invalidate()
                                }
                            }
                        }
                    }
                }
            }
        )

        // Hoja inferior con el formulario
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color.White
            ) {
                ReportFormSheet(
                    onDismiss = { showSheet = false },
                    onSubmit = {
                        Toast.makeText(
                            context,
                            "Reporte enviado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                        showSheet = false
                    }
                )
            }
        }
    }

    // Limpieza de MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                mapView?.onDetach()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}
