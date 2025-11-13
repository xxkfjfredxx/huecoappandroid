package com.fredrueda.huecoapp.feature.auth.data.remote.dto

data class RegisterResponse(
    val detail: String,
    val dev_code: String? = null
)