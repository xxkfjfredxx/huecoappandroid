package com.fredrueda.huecoapp.feature.map.presentation

import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class HuecoInfoWindow(
    context: Context,
    mapView: MapView,
    private val onClosed: () -> Unit,
    private val content: @Composable () -> Unit
) : InfoWindow(FrameLayout(context), mapView) {

    init {
        val composeView = ComposeView(context).apply {
            setContent { content() }
        }

        (mView as FrameLayout).apply {
            addView(composeView)
        }
    }

    override fun onOpen(item: Any?) {
        // No animation, no logic — safe.
    }

    override fun onClose() {
        onClosed()   // Notifica al ViewModel
    }
}
