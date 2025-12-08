package com.fredrueda.huecoapp.feature.report.data.remote.api

import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface HuecoApi {

    @Multipart
    @POST("api/v1/huecos/")
    suspend fun createHueco(
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): HuecoResponse

    @GET("api/v1/huecoscercanos/")
    suspend fun getHuecosCercanos(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("radio") radio: Int = 100
    ): Response<List<HuecoResponse>>

    @POST("api/v1/validaciones/")
    suspend fun validarHueco(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    @POST("api/v1/confirmaciones/")
    suspend fun confirmarHueco(
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
