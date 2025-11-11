package com.fredrueda.huecoapp.feature.home.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrawerItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : DrawerItem("Inicio", Icons.Default.Home, "home")
    object Map : DrawerItem("Mapa de huecos", Icons.Default.Map, "map")
    object Profile : DrawerItem("Perfil", Icons.Default.Person, "profile")
    object Logout : DrawerItem("Cerrar sesión", Icons.Default.ExitToApp, "logout")
}

val drawerItems = listOf(
    DrawerItem.Home,
    DrawerItem.Map,
    DrawerItem.Profile,
    DrawerItem.Logout
)
