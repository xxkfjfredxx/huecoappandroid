package com.fredrueda.huecoapp.feature.report.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HuecoResponse(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    val descripcion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val direccion: String?,
    val imagen: String?,
    val estado: String?,
    val verificado: Boolean?,
    @SerializedName("fecha_reporte") val fechaReporte: String?,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: String?,
    val comentarios: List<ComentarioResponse>?,
    @SerializedName("confirmaciones_count") val confirmacionesCount: Int?
)

data class ComentarioResponse(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    val texto: String?,
    val imagen: String?,
    val fecha: String?
)
