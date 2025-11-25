package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithFacebookUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(accessToken: String): ApiResponse<AuthUser> {
        return repo.loginWithFacebook(accessToken)
    }
}
