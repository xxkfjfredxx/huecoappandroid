package com.fredrueda.huecoapp.feature.home.data.remote.api

import com.fredrueda.huecoapp.feature.home.data.remote.dto.HuecoHomeDto
import com.fredrueda.huecoapp.feature.home.data.remote.dto.PagedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {

    @GET("api/v1/huecos/misreportes/")
    suspend fun getMisReportes(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Response<PagedResponse<HuecoHomeDto>>

    @GET("api/v1/huecos/seguidos/")
    suspend fun getSeguidos(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Response<PagedResponse<HuecoHomeDto>>
}
