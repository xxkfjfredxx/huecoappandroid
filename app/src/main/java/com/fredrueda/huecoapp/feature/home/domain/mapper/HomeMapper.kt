package com.fredrueda.huecoapp.feature.home.domain.mapper

import com.fredrueda.huecoapp.feature.home.data.remote.dto.HuecoHomeDto
import com.fredrueda.huecoapp.feature.home.model.HomeItem
import com.fredrueda.huecoapp.utils.constants.AppConstants

fun HuecoHomeDto.toHomeItem(tipo: String): HomeItem {

    val fullImageUrl = imagen?.let {
        if (it.startsWith("/")) AppConstants.BASE_URL + it
        else it
    }

    return HomeItem(
        id = id,
        titulo = descripcion ?: "Hueco $id",
        descripcion = descripcion ?: "",
        estado = when (estado) {
            "pendiente_validacion" -> "Pendiente"
            "activo" -> "Activo"
            "reabierto" -> "Reincidente"
            "cerrado" -> "Arreglado"
            else -> "Pendiente"
        },
        fecha = fechaReporte ?: "",
        tipo = tipo,
        imagen = fullImageUrl   // <-- LA URL COMPLETA
    )
}