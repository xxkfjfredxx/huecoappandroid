package com.fredrueda.huecoapp.feature.auth.data.remote.dto

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta del inicio de sesión.
 * 
 * @property access Token de acceso JWT (short-lived, expira en minutos/horas)
 * @property refresh Token de refresco JWT (long-lived, expira en días/semanas)
 * @property user Información del usuario autenticado
 */
data class LoginResponse(
    @SerializedName("access") val access: String?,
    @SerializedName("refresh") val refresh: String?,
    @SerializedName("user") val user: AuthUser?
)