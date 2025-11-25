package com.fredrueda.huecoapp.feature.auth.data.remote.dto

data class ResetPasswordRequest(
    val uid: String,
    val token: String,
    val password: String
)
