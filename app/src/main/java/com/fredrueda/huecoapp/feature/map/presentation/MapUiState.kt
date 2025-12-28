package com.fredrueda.huecoapp.feature.map.presentation

import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

data class MapUiState(
    val huecos: List<HuecoResponse> = emptyList(),
    val selectedHueco: HuecoResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null,      // 👈 AÑADIR ESTO
    val closeInfoWindow: Boolean = false, // bandera para cerrar el InfoWindow
    val reopenInfoWindowId: Int? = null // ID del hueco a reabrir
)
