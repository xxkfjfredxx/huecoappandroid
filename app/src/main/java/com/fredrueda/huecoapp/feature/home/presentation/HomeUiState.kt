package com.fredrueda.huecoapp.feature.home.presentation

import com.fredrueda.huecoapp.feature.home.model.HomeItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,

    val misReportes: List<HomeItem> = emptyList(),
    val seguidos: List<HomeItem> = emptyList(),

    // Paginación Mis reportes
    val misReportesOffset: Int = 0,
    val misReportesHasMore: Boolean = true,
    val isLoadingMoreMisReportes: Boolean = false,

    // Paginación Seguidos
    val seguidosOffset: Int = 0,
    val seguidosHasMore: Boolean = true,
    val isLoadingMoreSeguidos: Boolean = false
)

