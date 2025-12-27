package com.fredrueda.huecoapp.feature.huecos.data.repository

import com.fredrueda.huecoapp.feature.huecos.data.remote.api.HuecoDetailApi
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioPageResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import javax.inject.Inject

class HuecoDetailRepository @Inject constructor(
    private val api: HuecoDetailApi
) {
    suspend fun getHuecoDetail(id: Int): HuecoResponse = api.getHuecoDetail(id)
    suspend fun getComentarios(huecoId: Int, page: Int? = null, pageSize: Int? = null): ComentarioPageResponse =
        api.getComentarios(huecoId, page, pageSize)
}
