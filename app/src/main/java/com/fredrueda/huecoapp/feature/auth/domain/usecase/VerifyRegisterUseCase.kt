package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterVerifyRequest
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository

/**
 * Caso de uso para verificar el código OTP del registro.
 * 
 * Verifica el código de 6 dígitos enviado al correo del usuario
 * después de registrarse. Si es correcto, devuelve los tokens de
 * autenticación y completa el proceso de registro.
 * 
 * @param repository Repositorio de autenticación
 * @author Fred Rueda
 * @version 1.0
 */
class VerifyRegisterUseCase(
    private val repository: AuthRepository
) {
    /**
     * Ejecuta la verificación del código.
     * 
     * @param email Correo electrónico del usuario que se registró
     * @param code Código de verificación de 6 dígitos
     * @return ApiResponse con usuario autenticado y tokens, o error
     */
    suspend operator fun invoke(email: String, code: String): ApiResponse<AuthUser> =
        repository.verifyRegister(RegisterVerifyRequest(email, code))
}