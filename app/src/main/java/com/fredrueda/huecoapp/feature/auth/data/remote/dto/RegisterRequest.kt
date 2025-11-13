package com.fredrueda.huecoapp.feature.auth.data.remote.dto

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val first_name: String,
    val last_name: String
)