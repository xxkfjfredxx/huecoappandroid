package com.fredrueda.huecoapp.feature.auth.domain.entity

import com.google.gson.annotations.SerializedName

/**
 * Entidad de dominio que representa un usuario autenticado.
 *
 * Esta clase se usa tanto en:
 * - login (dentro de LoginResponse.user)
 * - endpoint /api/auth/me
 */
data class AuthUser(
    val id: Int?,

    val username: String?,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("last_name")
    val lastName: String?,

    val email: String?,

    @SerializedName("is_active")
    val isActive: Boolean?,

    @SerializedName("is_staff")
    val isStaff: Boolean?,

    @SerializedName("is_superuser")
    val isSuperuser: Boolean?,

    @SerializedName("auth_provider")
    val authProvider: String?,

    @SerializedName("employee_id")
    val employeeId: String?,

    // 🔹 NUEVOS CAMPOS DE /me
    @SerializedName("puntos_totales")
    val puntosTotales: Int?,

    @SerializedName("detalle_puntos")
    val detallePuntos: Map<String, Int>?,

    val reputacion: ReputacionDto?,

    val stats: StatsDto?,

    @SerializedName("foto_perfil")
    val fotoPerfil: String?,
)

/**
 * Bloque de reputación que viene en:
 *
 * "reputacion": {
 *   "nivel": "nuevo",
 *   "puntaje_total": 10
 * }
 */
data class ReputacionDto(
    val nivel: String?,

    @SerializedName("puntaje_total")
    val puntajeTotal: Int?,
)

/**
 * Bloque de estadísticas que viene en:
 *
 * "stats": {
 *   "reportes": 1,
 *   "huecos_seguidos": 0,
 *   "validaciones_realizadas": 0,
 *   "confirmaciones_realizadas": 0,
 *   "comentarios_realizados": 0
 * }
 */
data class StatsDto(
    val reportes: Int?,

    @SerializedName("huecos_seguidos")
    val huecosSeguidos: Int?,

    @SerializedName("validaciones_realizadas")
    val validacionesRealizadas: Int?,

    @SerializedName("confirmaciones_realizadas")
    val confirmacionesRealizadas: Int?,

    @SerializedName("comentarios_realizados")
    val comentariosRealizados: Int?,
)
