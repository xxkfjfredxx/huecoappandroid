package com.fredrueda.huecoapp.feature.home.data.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.home.data.remote.api.HomeApi
import com.fredrueda.huecoapp.feature.home.data.remote.dto.HuecoHomeDto
import com.fredrueda.huecoapp.feature.home.data.remote.dto.PagedResponse
import com.fredrueda.huecoapp.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getMisReportes(limit: Int, offset: Int): ApiResponse<PagedResponse<HuecoHomeDto>> {
        return try {
            val resp = api.getMisReportes(limit, offset)

            if (resp.isSuccessful) {
                resp.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.HttpError(resp.code(), "Respuesta vacía")
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun getSeguidos(
        limit: Int,
        offset: Int
    ): ApiResponse<PagedResponse<HuecoHomeDto>> {
        return try {
            val resp = api.getSeguidos(limit, offset)

            if (resp.isSuccessful) {
                resp.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.HttpError(resp.code(), "Respuesta vacía")
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e)
        }
    }


}
