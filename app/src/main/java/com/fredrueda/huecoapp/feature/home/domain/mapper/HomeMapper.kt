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
        estado = mapEstadoValueToLabel(estado),
        fecha = fechaReporte ?: "",
        tipo = tipo,
        imagen = fullImageUrl   // <-- LA URL COMPLETA
    )
}

fun HuecoHomeDto.toHomeItem(): HomeItem = HomeItem(
    id = id,
    titulo = descripcion ?: "",
    descripcion = descripcion ?: "",
    estado = mapEstadoValueToLabel(estado),
    fecha = fechaReporte ?: "",
    tipo = "", // Puedes ajustar si tienes un campo tipo
    imagen = imagen
)

// Mapea valores de estado que pueden venir como cadena descriptiva o como entero (o cadena numérica)
private fun mapEstadoValueToLabel(estadoRaw: Any?): String {
    if (estadoRaw == null) return "Pendiente"
    // Si es número
    when (estadoRaw) {
        is Number -> return mapEstadoIntToLabel(estadoRaw.toInt())
        is String -> {
            val num = estadoRaw.toIntOrNull()
            if (num != null) return mapEstadoIntToLabel(num)
            // comparar con strings conocidos
            return when (estadoRaw) {
                "pendiente_validacion" -> "Pendiente"
                "activo" -> "Activo"
                "reabierto" -> "Reincidente"
                "cerrado" -> "Arreglado"
                "en_reparacion" -> "En reparación"
                "reparado" -> "Reparado"
                "rechazado" -> "Rechazado"
                else -> estadoRaw.replaceFirstChar { it.uppercase() }
            }
        }
        else -> return estadoRaw.toString()
    }
}

private fun mapEstadoIntToLabel(estado: Int): String {
    return when (estado) {
        1 -> "Pendiente"
        2 -> "Activo"
        3 -> "Rechazado"
        4 -> "Reincidente"
        5 -> "Arreglado"
        6 -> "En reparación"
        7 -> "Reparado"
        else -> "Pendiente"
    }
}
