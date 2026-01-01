package com.fredrueda.huecoapp.feature.report.domain.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ConfirmacionResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse
import java.io.File

interface HuecoRepository {
    suspend fun crearHueco(
        latitud: Double,
        longitud: Double,
        descripcion: String,
        imagen: File?
    ): ApiResponse<HuecoResponse>

    suspend fun getHuecosCercanos(
        latitud: Double,
        longitud: Double,
        radio: Int
    ): ApiResponse<List<HuecoResponse>>
    suspend fun validarHueco(
        huecoId: Int,
        voto: Boolean
    ): ApiResponse<MiConfirmacionResponse>

    suspend fun confirmarHueco(
        huecoId: Int,
        nuevoEstado: Int
    ): ApiResponse<ConfirmacionResponse>

    suspend fun followHueco(huecoId: Int): ApiResponse<Unit>
    suspend fun unfollowHueco(huecoId: Int): ApiResponse<Unit>
}
