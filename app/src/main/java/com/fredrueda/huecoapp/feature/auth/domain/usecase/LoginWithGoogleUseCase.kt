package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para iniciar sesión con Google Sign-In.
 * 
 * Maneja el login social usando Google OAuth 2.0.
 * Recibe el ID Token de Google y lo envía al backend
 * para autenticar o crear el usuario.
 * 
 * @param repo Repositorio de autenticación
 * @author Fred Rueda
 * @version 1.0
 */
class LoginWithGoogleUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    /**
     * Ejecuta el login con Google.
     * 
     * @param idToken Token de identificación de Google
     * @return ApiResponse con usuario autenticado o error
     */
    suspend operator fun invoke(idToken: String): ApiResponse<AuthUser> {
        return repo.loginWithGoogle(idToken)
    }
}
