package com.fredrueda.huecoapp.feature.report.data.remote.api

import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface HuecoApi {

    @Multipart
    @POST("api/v1/huecos/")
    suspend fun createHueco(
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): HuecoResponse
}
