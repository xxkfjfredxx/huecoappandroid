package com.fredrueda.huecoapp.feature.huecos.data.repository

import com.fredrueda.huecoapp.feature.huecos.data.remote.api.HuecoDetailApi
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioPageResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.CreateComentarioRequest
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import javax.inject.Inject

class HuecoDetailRepository @Inject constructor(
    private val api: HuecoDetailApi
) {
    suspend fun getHuecoDetail(id: Int): HuecoResponse = api.getHuecoDetail(id)
    suspend fun getComentarios(huecoId: Int, page: Int? = null, pageSize: Int? = null): ComentarioPageResponse =
        api.getComentarios(huecoId, page, pageSize)

    suspend fun createComentario(request: CreateComentarioRequest): ComentarioResponse = api.createComentario(request)

    suspend fun followHueco(id: Int): Boolean {
        val response = api.followHueco(id)
        val detail = response.body()?.get("detail") ?: ""
        val success = response.isSuccessful && (detail.contains("sigues") || detail.contains("seguido"))
        return success
    }

    suspend fun unfollowHueco(id: Int): Boolean {
        val response = api.unfollowHueco(id)
        val success = response.isSuccessful && response.body()?.get("detail")?.contains("dejado de seguir") == true
        return success
    }

    suspend fun toggleFollow(id: Int, isFollowed: Boolean): Boolean {
        return if (isFollowed) {
            unfollowHueco(id)
        } else {
            followHueco(id)
        }
    }
}
