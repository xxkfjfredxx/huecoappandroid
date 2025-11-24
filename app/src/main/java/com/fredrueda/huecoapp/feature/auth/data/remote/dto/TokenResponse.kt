package com.fredrueda.huecoapp.feature.auth.data.remote.dto

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta con tokens de autenticación.
 * 
 * Utilizado en la verificación de registro y otros flujos de autenticación.
 * 
 * @property access Token de acceso JWT (short-lived)
 * @property refresh Token de refresco JWT (long-lived)
 * @property user Información del usuario autenticado
 */
data class TokenResponse(
    @SerializedName("access") val access: String?,
    @SerializedName("refresh") val refresh: String?,
    @SerializedName("user") val user: AuthUser?
)