package com.fredrueda.huecoapp.feature.report.data.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.data.remote.api.HuecoApi
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse
import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class HuecoRepositoryImpl @Inject constructor(
    private val api: HuecoApi
) : HuecoRepository {

    override suspend fun crearHueco(
        latitud: Double,
        longitud: Double,
        descripcion: String,
        imagen: File?
    ): ApiResponse<HuecoResponse> {

        val latBody = latitud.toString().toRequestBody("text/plain".toMediaType())
        val lonBody = longitud.toString().toRequestBody("text/plain".toMediaType())
        val descBody = descripcion.toRequestBody("text/plain".toMediaType())

        val imgPart = imagen?.let {
            val requestFile = it.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("imagen", it.name, requestFile)
        }

        return try {
            val response = api.createHueco(
                latitud = latBody,
                longitud = lonBody,
                descripcion = descBody,
                imagen = imgPart
            )

            ApiResponse.Success(response)

        } catch (e: HttpException) {
            ApiResponse.HttpError(e.code(), e.message())

        } catch (e: IOException) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun getHuecosCercanos(
        latitud: Double,
        longitud: Double,
        radio: Int
    ): ApiResponse<List<HuecoResponse>> {
        return try {
            val resp = api.getHuecosCercanos(
                latitud = latitud,
                longitud = longitud,
                radio = radio
            )

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

    override suspend fun validarHueco(
        huecoId: Int,
        voto: Boolean
    ): ApiResponse<MiConfirmacionResponse> {
        return try {
            val body = mapOf(
                "hueco" to huecoId,
                "voto" to voto
            )
            val resp = api.validarHueco(body)

            if (resp.isSuccessful) {
                resp.body()?.let { ApiResponse.Success(it) }
                    ?: ApiResponse.HttpError(resp.code(), "Respuesta vacía")
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun confirmarHueco(
        huecoId: Int,
        confirmado: Boolean
    ): ApiResponse<Unit> {
        return try {
            val body = mapOf(
                "hueco" to huecoId,
                "confirmado" to confirmado
            )
            val resp = api.confirmarHueco(body)

            if (resp.isSuccessful) {
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun followHueco(huecoId: Int): ApiResponse<Unit> {
        return try {
            val response = api.followHueco(huecoId)
            if (response.isSuccessful) {
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.HttpError(response.code(), response.message())
            }
        } catch (e: HttpException) {
            ApiResponse.HttpError(e.code(), e.message())
        } catch (e: IOException) {
            ApiResponse.NetworkError(e)
        }
    }

    override suspend fun unfollowHueco(huecoId: Int): ApiResponse<Unit> {
        return try {
            val response = api.unfollowHueco(huecoId)
            if (response.isSuccessful) {
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.HttpError(response.code(), response.message())
            }
        } catch (e: HttpException) {
            ApiResponse.HttpError(e.code(), e.message())
        } catch (e: IOException) {
            ApiResponse.NetworkError(e)
        }
    }


}