package com.fredrueda.huecoapp.feature.auth.data.remote.dto

import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access") val access: String?,
    @SerializedName("refresh") val refresh: String?,
    @SerializedName("user") val user: AuthUser?
)