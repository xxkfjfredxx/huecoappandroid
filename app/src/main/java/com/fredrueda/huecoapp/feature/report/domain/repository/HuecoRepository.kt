package com.fredrueda.huecoapp.feature.report.domain.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import java.io.File

interface HuecoRepository {
    suspend fun crearHueco(
        latitud: Double,
        longitud: Double,
        descripcion: String,
        imagen: File?
    ): ApiResponse<HuecoResponse>
}
