package com.fredrueda.huecoapp.feature.auth.presentation

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser

data class AuthUiState(
    val user: AuthUser? = null,
    val access: String? = null,
    val refresh: String? = null,
    val isLoggedIn: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val devCode: String? = null,
    val isSuccess: Boolean = false
)

data class VerifyRegisterState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null
)