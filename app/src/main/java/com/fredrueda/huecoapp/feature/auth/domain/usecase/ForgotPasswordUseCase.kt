package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.feature.auth.data.remote.dto.ForgotPasswordRequest
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para solicitar recuperación de contraseña.
 */
class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String) =
        repository.forgotPassword(ForgotPasswordRequest(email = email))
}
