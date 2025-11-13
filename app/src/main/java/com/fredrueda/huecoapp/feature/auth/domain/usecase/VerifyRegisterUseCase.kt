package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterVerifyRequest
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository

class VerifyRegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String): ApiResponse<AuthUser> =
        repository.verifyRegister(RegisterVerifyRequest(email, code))
}