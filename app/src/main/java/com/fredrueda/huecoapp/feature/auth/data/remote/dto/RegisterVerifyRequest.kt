package com.fredrueda.huecoapp.feature.auth.data.remote.dto

/**
 * DTO para la petición de verificación de registro.
 * 
 * @property email Correo electrónico del usuario que se está registrando
 * @property code Código de verificación OTP de 6 dígitos
 */
data class RegisterVerifyRequest(
    val email: String,
    val code: String
)