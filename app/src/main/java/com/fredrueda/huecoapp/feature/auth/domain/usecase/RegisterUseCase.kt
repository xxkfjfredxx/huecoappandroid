package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterRequest
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ) = repository.register(
        RegisterRequest(
            email = email,
            password = password,
            username = username,
            first_name = firstName,
            last_name = lastName
        )
    )
}
