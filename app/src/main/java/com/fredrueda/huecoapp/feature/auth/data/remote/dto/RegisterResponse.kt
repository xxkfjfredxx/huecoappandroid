package com.fredrueda.huecoapp.feature.auth.data.remote.dto

/**
 * DTO para la respuesta del registro de usuario.
 * 
 * @property detail Mensaje descriptivo de la respuesta (ej: "Código enviado a tu email")
 * @property dev_code Código de verificación (solo en entorno de desarrollo para facilitar pruebas)
 */
data class RegisterResponse(
    val detail: String,
    val dev_code: String? = null
)