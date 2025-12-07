package com.fredrueda.huecoapp.feature.home.domain.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.home.data.remote.dto.HuecoHomeDto
import com.fredrueda.huecoapp.feature.home.data.remote.dto.PagedResponse

interface HomeRepository {
    suspend fun getMisReportes(limit: Int, offset: Int): ApiResponse<PagedResponse<HuecoHomeDto>>
    suspend fun getSeguidos(limit: Int, offset: Int): ApiResponse<PagedResponse<HuecoHomeDto>>

}