package com.fredrueda.huecoapp.feature.report.data.remote.dto

import com.squareup.moshi.Json

data class HuecoResponse(
    val id: Int,
    val usuario: Int?,
    @Json(name = "usuario_nombre") val usuarioNombre: String?,
    val descripcion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val direccion: String?,
    val imagen: String?,
    val estado: String?,
    val verificado: Boolean?,
    @Json(name = "fecha_reporte") val fechaReporte: String?,
    @Json(name = "fecha_actualizacion") val fechaActualizacion: String?,
    val comentarios: List<ComentarioResponse>?,
    @Json(name = "confirmaciones_count") val confirmacionesCount: Int?
)

data class ComentarioResponse(
    val id: Int,
    val usuario: Int?,
    @Json(name = "usuario_nombre") val usuarioNombre: String?,
    val texto: String?,
    val imagen: String?,
    val fecha: String?
)
