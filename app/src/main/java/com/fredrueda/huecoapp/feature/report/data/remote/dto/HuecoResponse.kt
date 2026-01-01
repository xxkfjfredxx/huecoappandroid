package com.fredrueda.huecoapp.feature.report.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HuecoResponse(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    val ciudad: String?,
    val descripcion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val estado: String?,
    @SerializedName("fecha_reporte") val fechaReporte: String?,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: String?,
    @SerializedName("numero_ciclos") val numeroCiclos: Int?,
    @SerializedName("validaciones_positivas") val validacionesPositivas: Int?,
    @SerializedName("validaciones_negativas") val validacionesNegativas: Int?,
    val gravedad: String?,
    val vistas: Int?,
    val imagen: String?,
    val comentarios: List<ComentarioResponse>?,
    @SerializedName("total_comentarios") val totalComentarios: Int?,
    @SerializedName("confirmaciones_count") val confirmacionesCount: Int?,
    @SerializedName("validado_usuario") val validadoUsuario: Boolean?,
    @SerializedName("mi_confirmacion") val miConfirmacion: MiConfirmacionResponse?,
    @SerializedName("faltan_validaciones") val faltanValidaciones: Int?,
    @SerializedName("is_followed") val isFollowed: Boolean?
) : Parcelable

@Parcelize
data class ComentarioResponse(
    val id: Int,
    val usuario: Int?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    val texto: String?,
    val imagen: String?,
    val fecha: String?
) : Parcelable

// DTO para paginación de comentarios
data class ComentarioPageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ComentarioResponse>
)

@Parcelize
data class MiConfirmacionResponse(
    val id: Int?,
    val hueco: Int?,
    val usuario: Int?,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    val confirmado: Boolean?,
    val fecha: String?,
    @SerializedName("voto") val voto: Boolean?, // <-- AGREGADO
    @SerializedName("nuevo_estado") val nuevoEstado: Int? // nuevo campo que puede venir
) : Parcelable

// DTO para la respuesta de confirmar/crear una confirmación (nuevo_estado)
data class ConfirmacionResponse(
    val id: Int,
    val hueco: Int,
    val usuario: Int,
    @SerializedName("usuario_nombre") val usuarioNombre: String?,
    @SerializedName("nuevo_estado") val nuevoEstado: Int,
    val fecha: String?
)

// DTO para crear comentario (request body)
data class CreateComentarioRequest(
    val hueco: Int,
    val texto: String,
    val imagen: String? = null
)
