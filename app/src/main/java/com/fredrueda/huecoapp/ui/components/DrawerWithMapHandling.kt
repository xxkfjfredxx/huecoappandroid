package com.fredrueda.huecoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.launch

/**
 * Modifier helper para clicks sin ripple.
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) { onClick() }
}

/**
 * Drawer que deshabilita gestos en pantalla de mapa y permite cerrar con tap.
 * @param selectedRoute ruta actual
 * @param drawerState estado del drawer
 * @param onCloseDrawer acción suspend para cerrar
 * @param drawerContent contenido del drawer
 * @param content contenido principal
 */
@Composable
fun DrawerWithMapHandling(
    selectedRoute: String,
    drawerState: DrawerState,
    onCloseDrawer: suspend () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || selectedRoute != "map",
        drawerContent = drawerContent
    ) {
        Scaffold { innerPadding ->
            Box(Modifier.fillMaxSize()) {
                // Contenido principal
                content(innerPadding)

                // Capa invisible para cerrar drawer con tap
                if (drawerState.isOpen) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                scope.launch { onCloseDrawer() }
                            }
                    )
                }
            }
        }
    }
}
