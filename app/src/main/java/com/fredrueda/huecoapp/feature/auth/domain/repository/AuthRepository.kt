package com.fredrueda.huecoapp.feature.auth.domain.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.ForgotPasswordRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterVerifyRequest
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser

/**
 * Interfaz del repositorio de autenticación.
 * 
 * Define los contratos (métodos) que debe implementar cualquier repositorio
 * de autenticación. Esta abstracción permite desacoplar la capa de dominio
 * de la implementación concreta (que puede ser red, base de datos, etc.).
 * 
 * Operaciones soportadas:
 * - Login con email/password
 * - Login social (Google, Facebook)
 * - Registro de nuevos usuarios
 * - Verificación de código OTP
 * - Obtener información del usuario actual
 * - Cerrar sesión
 * 
 * @author Fred Rueda
 * @version 1.0
 */
interface AuthRepository {
    
    /**
     * Inicia sesión con credenciales de email y contraseña.
     * 
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return ApiResponse con el usuario autenticado o error
     */
    suspend fun login(email: String, password: String): ApiResponse<AuthUser>
    
    /**
     * Inicia sesión con Google Sign-In.
     * 
     * @param idToken Token de identificación de Google
     * @return ApiResponse con el usuario autenticado o error
     */
    suspend fun loginWithGoogle(idToken: String): ApiResponse<AuthUser>
    
    /**
     * Inicia sesión con Facebook Login.
     * 
     * @param accessToken Token de acceso de Facebook
     * @return ApiResponse con el usuario autenticado o error
     */
    suspend fun loginWithFacebook(accessToken: String): ApiResponse<AuthUser>
    
    /**
     * Obtiene la información del usuario actualmente autenticado.
     * 
     * @return ApiResponse con la información del usuario o error
     */
    suspend fun me(): ApiResponse<AuthUser>
    
    /**
     * Cierra la sesión del usuario actual.
     * 
     * @return ApiResponse indicando éxito o error
     */
    suspend fun logout(): ApiResponse<Unit>
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param request Datos del nuevo usuario (email, password, nombre, etc.)
     * @return RegisterResponse con mensaje y código de verificación
     */
    suspend fun register(request: RegisterRequest): RegisterResponse
    
    /**
     * Verifica el código OTP enviado por correo después del registro.
     * 
     * @param request Petición con email y código de verificación
     * @return ApiResponse con tokens de autenticación o error
     */
    suspend fun verifyRegister(request: RegisterVerifyRequest): ApiResponse<AuthUser>

    /**
     * Envía el correo de recuperación de contraseña.
     *
     * @param request Contiene el email del usuario.
     * @return RegisterResponse con el detail del backend.
     */
    suspend fun forgotPassword(request: ForgotPasswordRequest): RegisterResponse

    suspend fun resetPassword(uid: String, token: String, password: String): RegisterResponse

}
