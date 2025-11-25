package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para iniciar sesión con email y contraseña.
 * 
 * Encapsula la lógica de negocio para el login estándar.
 * Este caso de uso es invocado por el ViewModel y delega
 * la operación al repositorio.
 * 
 * Patrón Use Case:
 * - Una responsabilidad única: login con credenciales
 * - Independiente de la UI y frameworks
 * - Fácil de testear unitariamente
 * 
 * @param repo Repositorio de autenticación
 * @author Fred Rueda
 * @version 1.0
 */
class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    /**
     * Ejecuta el caso de uso de login.
     * 
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return ApiResponse con el usuario autenticado o error
     */
    suspend operator fun invoke(email: String, password: String): ApiResponse<AuthUser> =
        repo.login(email, password)
}
