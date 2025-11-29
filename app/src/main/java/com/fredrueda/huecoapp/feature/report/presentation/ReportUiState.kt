package com.fredrueda.huecoapp.feature.report.presentation

import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

data class ReportUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val hueco: HuecoResponse? = null
)
