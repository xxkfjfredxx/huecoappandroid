package com.fredrueda.huecoapp.feature.auth.data.remote.dto

/**
 * DTO para solicitar recuperación de contraseña.
 *
 * Backend espera:
 * {
 *   "email": "usuario@dominio.com"
 * }
 */
data class ForgotPasswordRequest(
    val email: String
)
