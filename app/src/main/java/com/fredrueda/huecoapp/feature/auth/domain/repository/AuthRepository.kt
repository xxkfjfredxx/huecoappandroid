package com.fredrueda.huecoapp.feature.auth.domain.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterRequest
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterVerifyRequest
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResponse<AuthUser>
    suspend fun loginWithGoogle(idToken: String): ApiResponse<AuthUser>
    suspend fun loginWithFacebook(accessToken: String): ApiResponse<AuthUser>
    suspend fun me(): ApiResponse<AuthUser>
    suspend fun logout(): ApiResponse<Unit>
    suspend fun register(request: RegisterRequest): RegisterResponse
    suspend fun verifyRegister(request: RegisterVerifyRequest): ApiResponse<AuthUser>
}
