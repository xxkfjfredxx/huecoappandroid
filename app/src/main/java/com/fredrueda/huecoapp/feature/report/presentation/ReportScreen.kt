package com.fredrueda.huecoapp.feature.report.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun ReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ReportViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // --- Estado UI
    var mapView: MapView? by remember { mutableStateOf(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var showTooltip by remember { mutableStateOf(true) }
    var isDraggingMarker by remember { mutableStateOf(false) } // 👈 NUEVO estado

    // --- Pide permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasLocationPermission =
            perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )

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
    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, "Reporte creado con éxito", Toast.LENGTH_SHORT).show()
            showSheet = false
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
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
            // 👇 FAB animado, se oculta mientras se arrastra el marcador
            AnimatedVisibility(
                visible = !isDraggingMarker,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },
                    text = { Text("Reportar") },
                    containerColor = Color(0xFFFFD000),
                    contentColor = Color.Black,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) { innerPadding ->

        Box(Modifier.fillMaxSize()) {
            // --- Mapa principal
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

                        // Centro temporal mientras llega ubicación real
                        val fallback = GeoPoint(4.787316, -74.229235)
                        controller.setZoom(17.0)
                        controller.setCenter(fallback)

                        // --- Marcador arrastrable
                        val marker = Marker(this).apply {
                            position = fallback
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Ubicación del hueco"
                            isDraggable = true
                            icon = ContextCompat.getDrawable(ctx, R.drawable.ic_huecoapp)

                            setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                                override fun onMarkerDrag(marker: Marker?) {
                                    isDraggingMarker = true // 👈 mientras se arrastra
                                }

                                override fun onMarkerDragEnd(marker: Marker?) {
                                    marker?.let { userLocation = it.position }
                                    isDraggingMarker = false // 👈 al soltar vuelve visible FAB
                                    //Toast.makeText(ctx, "Marcador ubicado. Este será el punto del reporte.", Toast.LENGTH_SHORT).show()
                                }

                                override fun onMarkerDragStart(marker: Marker?) {
                                    isDraggingMarker = true
                                }
                            })
                        }
                        overlays.add(marker)

                        // Capa de ubicación real
                        if (hasLocationPermission) {
                            val locationOverlay =
                                MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                            locationOverlay.enableMyLocation()
                            locationOverlay.enableFollowLocation()
                            overlays.add(locationOverlay)

                            locationOverlay.runOnFirstFix {
                                val loc: GeoPoint? = locationOverlay.myLocation
                                loc?.let { point ->
                                    post {
                                        userLocation = point
                                        controller.setCenter(point)
                                        controller.setZoom(22.0)
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
                    if (hasLocationPermission) {
                        val alreadyAdded = map.overlays.any { it is MyLocationNewOverlay }
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
                                        map.controller.setZoom(20.0)
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

            // --- Tooltip explicativo inicial
            if (showTooltip && userLocation != null) {
                isDraggingMarker = true
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000))
                        .clickable { showTooltip = false }
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = (-80).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 8.dp
                        ) {
                            Text(
                                text = "Mantén presionado el marcador y arrástralo para ubicar el hueco",
                                color = Color.Black,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                showTooltip = false
                                isDraggingMarker = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD000),
                                contentColor = Color.Black
                            )
                        ) {
                            Text("OK, entendido")
                        }
                    }
                }
            }

            // --- Hoja inferior con el formulario
            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = bottomSheetState,
                    containerColor = Color.White
                ) {
                    ReportFormSheet(
                        onDismiss = { showSheet = false },
                        onSubmit = { description, base64Image ->

                            // Convertir Base64 a File (solo si existe)
                            val imageFile = base64Image?.let { b64 ->
                                val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                                val tempFile = File(
                                    context.cacheDir,
                                    "hueco_${System.currentTimeMillis()}.jpg"
                                )
                                tempFile.writeBytes(bytes)
                                tempFile
                            }

                            // Obtener lat/lon del marcador
                            val point = userLocation
                            if (point == null) {
                                Toast.makeText(context, "Ubicación no detectada todavía", Toast.LENGTH_SHORT).show()
                                return@ReportFormSheet
                            }

                            // Llamado al ViewModel EXACTO
                            viewModel.crearHueco(
                                latitud = point.latitude,
                                longitud = point.longitude,
                                descripcion = description,
                                imagen = imageFile
                            )

                            // Cerrar sheet (esto se reactiva cuando success sea true)
                            // showSheet = false
                        }
                    )
                }
            }
        }
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
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
