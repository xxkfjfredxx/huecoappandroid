package com.fredrueda.huecoapp.feature.auth.presentation

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser

data class AuthUiState(
    val loading: Boolean = false,
    val user: AuthUser? = null,
    val error: String? = null
)
