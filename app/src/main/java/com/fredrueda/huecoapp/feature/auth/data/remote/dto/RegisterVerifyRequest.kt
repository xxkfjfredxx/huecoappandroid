package com.fredrueda.huecoapp.feature.auth.data.remote.dto

data class RegisterVerifyRequest(
    val email: String,
    val code: String
)