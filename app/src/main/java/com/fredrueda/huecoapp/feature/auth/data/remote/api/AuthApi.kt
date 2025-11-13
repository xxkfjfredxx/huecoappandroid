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

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): Response<Map<String, String>> // {access}

    @GET("api/auth/me")
    suspend fun me(): Response<AuthUser>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/auth/google-login/")
    suspend fun loginWithGoogle(@Body body: Map<String, String>): Response<LoginResponse>

    @POST("api/auth/facebook/")
    suspend fun loginWithFacebook(@Body body: Map<String, String>): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("api/auth/register/verify")
    suspend fun verifyRegister(@Body body: RegisterVerifyRequest): Response<TokenResponse>
}