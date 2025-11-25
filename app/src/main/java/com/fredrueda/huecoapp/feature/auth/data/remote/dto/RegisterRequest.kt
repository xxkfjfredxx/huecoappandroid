package com.fredrueda.huecoapp.feature.auth.data.remote.dto

/**
 * DTO para la petición de registro de nuevo usuario.
 * 
 * @property email Correo electrónico del usuario
 * @property password Contraseña del usuario
 * @property username Nombre de usuario único
 * @property first_name Nombre(s) del usuario
 * @property last_name Apellido(s) del usuario
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val first_name: String,
    val last_name: String
)