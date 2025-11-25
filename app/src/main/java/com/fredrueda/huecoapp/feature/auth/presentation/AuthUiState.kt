package com.fredrueda.huecoapp.feature.auth.presentation

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser

/**
 * Estado de la UI para autenticación (Login).
 * 
 * Representa el estado completo de la pantalla de login,
 * incluyendo información del usuario, tokens y estados de carga/error.
 * 
 * @property user Información del usuario autenticado (null si no hay sesión)
 * @property access Token de acceso JWT
 * @property refresh Token de refresco JWT
 * @property isLoggedIn Indica si el usuario inició sesión exitosamente
 * @property loading Indica si hay una operación en curso (login, Google, Facebook)
 * @property error Mensaje de error (null si no hay error)
 */
data class AuthUiState(
    val user: AuthUser? = null,
    val access: String? = null,
    val refresh: String? = null,
    val isLoggedIn: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la UI para registro de usuario.
 * 
 * Representa el estado del proceso de registro, desde la petición
 * hasta la recepción del código de verificación.
 * 
 * @property isLoading Indica si la petición de registro está en curso
 * @property message Mensaje de respuesta del servidor
 * @property devCode Código de verificación (solo para desarrollo/debug)
 * @property isSuccess Indica si el registro fue exitoso
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val devCode: String? = null,
    val isSuccess: Boolean = false
)

/**
 * Estado de la UI para verificación de registro.
 * 
 * Representa el estado del proceso de verificación del código OTP
 * enviado al correo del usuario.
 * 
 * @property isLoading Indica si la verificación está en curso
 * @property isVerified Indica si el código fue verificado correctamente
 * @property error Mensaje de error (null si no hay error)
 */
data class VerifyRegisterState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la UI para el flujo de "Olvidé mi contraseña".
 *
 * @property isLoading Indica si se está enviando el correo
 * @property message Mensaje de éxito del backend (detail)
 * @property error Mensaje de error (null si no hay error)
 */
data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

data class ResetPasswordState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)