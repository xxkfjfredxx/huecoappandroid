package com.fredrueda.huecoapp.feature.home.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HuecoHomeDto(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre")
    val usuarioNombre: String?,
    val descripcion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val estado: String?,
    @SerializedName("fecha_reporte")
    val fechaReporte: String?,
    @SerializedName("fecha_actualizacion")
    val fechaActualizacion: String?,
    @SerializedName("numero_ciclos")
    val numeroCiclos: Int?,
    @SerializedName("validaciones_positivas")
    val validacionesPos: Int?,
    @SerializedName("validaciones_negativas")
    val validacionesNeg: Int?,
    val imagen: String?,
    val comentarios: List<CommentHomeDto>?,
    @SerializedName("confirmaciones_count")
    val confirmacionesCount: Int?
)

data class CommentHomeDto(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre")
    val usuarioNombre: String?,
    val texto: String?,
    val imagen: String?,
    val fecha: String?
)
