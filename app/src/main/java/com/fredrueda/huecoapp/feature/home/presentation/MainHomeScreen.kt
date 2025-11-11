package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fredrueda.huecoapp.feature.home.model.HomeItem
import com.fredrueda.huecoapp.feature.map.presentation.MapScreen
import com.fredrueda.huecoapp.feature.profile.presentation.ProfileScreen
import kotlinx.coroutines.launch

// 🔹 Datos quemados (temporales)
val huecos = listOf(
    HomeItem(1, "Hueco en la calle 10", "Bache profundo cerca de la esquina", "Pendiente", "Hace 2h", "Reportado"),
    HomeItem(2, "Avenida del Poblado", "Reparación en curso", "En reparación", "Hace 5h", "Siguiendo"),
    HomeItem(3, "Calle 80", "Ya fue arreglado recientemente", "Arreglado", "Ayer", "Reincidente"),
    HomeItem(4, "Carrera 45", "Bache pequeño con riesgo", "Pendiente", "Hace 3h", "Reportado"),
    HomeItem(1, "Hueco en la calle 10", "Bache profundo cerca de la esquina", "Pendiente", "Hace 2h", "Reportado"),
    HomeItem(2, "Avenida del Poblado", "Reparación en curso", "En reparación", "Hace 5h", "Siguiendo"),
    HomeItem(3, "Calle 80", "Ya fue arreglado recientemente", "Arreglado", "Ayer", "Reincidente"),
    HomeItem(4, "Carrera 45", "Bache pequeño con riesgo", "Pendiente", "Hace 3h", "Reportado"),
    HomeItem(1, "Hueco en la calle 10", "Bache profundo cerca de la esquina", "Pendiente", "Hace 2h", "Reportado"),
    HomeItem(2, "Avenida del Poblado", "Reparación en curso", "En reparación", "Hace 5h", "Siguiendo"),
    HomeItem(3, "Calle 80", "Ya fue arreglado recientemente", "Arreglado", "Ayer", "Reincidente"),
    HomeItem(4, "Carrera 45", "Bache pequeño con riesgo", "Pendiente", "Hace 3h", "Reportado"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onLogout: () -> Unit = {},
    onNavigateToMap: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedRoute by remember { mutableStateOf("home") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = selectedRoute != "map",
        scrimColor = Color.Black.copy(alpha = 0.20f),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f),
                drawerContainerColor = Color.White,
                drawerTonalElevation = 12.dp,
                drawerShape = MaterialTheme.shapes.large
            ) {
                HomeDrawerContent(
                    selectedRoute = selectedRoute
                ) { item ->
                    selectedRoute = item.route
                    scope.launch { drawerState.close() }
                    if (item == DrawerItem.Logout) onLogout()
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (selectedRoute) {
                                "home" -> "Mis reportes"
                                "map" -> "Mapa de huecos"
                                "profile" -> "Perfil"
                                else -> ""
                            },
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    )
                )
            },
            containerColor = Color(0xFFF7F7F7)
        ) { innerPadding ->

            // 🔹 Usa Crossfade para transiciones suaves entre pantallas
            Crossfade(targetState = selectedRoute, label = "drawer_transition") { route ->
                when (route) {
                    "home" -> HomeScreen(
                        huecos = huecos,
                        onReportClick = onNavigateToMap,
                        modifier = Modifier.padding(innerPadding) // ✅ evita que se tape con el TopAppBar
                    )

                    "map" -> MapScreen()

                    "profile" -> ProfileScreen(
                        modifier = Modifier.padding(innerPadding) // ✅ también lo aplica al perfil
                    )
                }
            }
        }
    }
}
