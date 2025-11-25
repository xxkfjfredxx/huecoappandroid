package com.fredrueda.huecoapp.feature.auth.domain.entity

/**
 * Entidad de dominio que representa un usuario autenticado.
 * 
 * @property id Identificador único del usuario
 * @property username Nombre de usuario
 * @property firstName Nombre(s) del usuario
 * @property lastName Apellido(s) del usuario
 * @property email Correo electrónico
 * @property isActive Indica si la cuenta está activa
 * @property isStaff Indica si el usuario es parte del staff
 * @property isSuperuser Indica si el usuario es superusuario/administrador
 * @property authProvider Proveedor de autenticación (email, google, facebook)
 * @property employeeId ID del empleado (si aplica)
 */
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