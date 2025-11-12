package com.fredrueda.huecoapp.feature.auth.domain.entity

data class AuthUser(
    val id: Int?,
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val isActive: Boolean?,
    val isStaff: Boolean?,
    val isSuperuser: Boolean?,
    val authProvider: String?,
    val employeeId: String?
)