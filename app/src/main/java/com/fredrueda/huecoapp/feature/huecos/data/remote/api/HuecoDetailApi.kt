package com.fredrueda.huecoapp.feature.huecos.data.remote.api

import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioPageResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HuecoDetailApi {
    @GET("api/v1/huecos/{id}/")
    suspend fun getHuecoDetail(@Path("id") id: Int): HuecoResponse

    @GET("/api/v1/comentarios/")
    suspend fun getComentarios(
        @Query("hueco") huecoId: Int,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): ComentarioPageResponse
}
