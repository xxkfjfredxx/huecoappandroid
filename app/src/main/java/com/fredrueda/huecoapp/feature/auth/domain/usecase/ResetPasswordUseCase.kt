package com.fredrueda.huecoapp.feature.auth.domain.use_case

import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterResponse
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String, token: String, password: String): RegisterResponse {
        return repository.resetPassword(uid, token, password)
    }
}
