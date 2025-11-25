package com.fredrueda.huecoapp.feature.auth.data.remote.dto

/**
 * DTO para la petición de inicio de sesión.
 * 
 * @property email Correo electrónico del usuario
 * @property password Contraseña del usuario
 */
data class LoginRequest(
    val email: String,
    val password: String
)