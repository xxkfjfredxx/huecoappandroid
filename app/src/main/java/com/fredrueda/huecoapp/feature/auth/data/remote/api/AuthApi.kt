package com.fredrueda.huecoapp.feature.auth.data.remote.api

import com.fredrueda.huecoapp.feature.auth.data.remote.dto.LoginRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.LoginResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterVerifyRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.TokenResponse
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz de Retrofit para el API de autenticación.
 * 
 * Define todos los endpoints relacionados con autenticación de usuarios,
 * incluyendo login, registro, login social, tokens y sesión.
 * 
 * Retrofit genera automáticamente la implementación de esta interfaz
 * en tiempo de ejecución.
 * 
 * @author Fred Rueda
 * @version 1.0
 */
interface AuthApi {
    
    /**
     * Inicia sesión con email y contraseña.
     * 
     * @param body Credenciales del usuario (email, password)
     * @return Response con tokens y datos del usuario
     */
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    /**
     * Refresca el token de acceso usando el token de refresco.
     * 
     * @param body Mapa con el refresh token
     * @return Response con nuevo access token
     */
    @POST("api/auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): Response<Map<String, String>>

    /**
     * Obtiene la información del usuario actualmente autenticado.
     * 
     * Requiere header Authorization con Bearer token.
     * 
     * @return Response con datos del usuario
     */
    @GET("api/auth/me")
    suspend fun me(): Response<AuthUser>

    /**
     * Cierra la sesión del usuario actual.
     * 
     * Invalida el refresh token en el servidor.
     * 
     * @return Response vacía indicando éxito o error
     */
    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    /**
     * Inicia sesión con Google Sign-In.
     * 
     * @param body Mapa con el ID token de Google
     * @return Response con tokens y datos del usuario
     */
    @POST("api/auth/google-login/")
    suspend fun loginWithGoogle(@Body body: Map<String, String>): Response<LoginResponse>

    /**
     * Inicia sesión con Facebook Login.
     * 
     * @param body Mapa con el access token de Facebook
     * @return Response con tokens y datos del usuario
     */
    @POST("api/auth/facebook/")
    suspend fun loginWithFacebook(@Body body: Map<String, String>): Response<LoginResponse>

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Envía un código de verificación al email del usuario.
     * 
     * @param body Datos del nuevo usuario (email, password, nombre, etc.)
     * @return RegisterResponse con mensaje y código (en desarrollo)
     */
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    /**
     * Verifica el código OTP del registro.
     * 
     * @param body Email y código de verificación
     * @return Response con tokens de autenticación
     */
    @POST("api/auth/register/verify")
    suspend fun verifyRegister(@Body body: RegisterVerifyRequest): Response<TokenResponse>
}