package com.fredrueda.huecoapp.feature.auth.domain.usecase

import com.fredrueda.huecoapp.feature.auth.data.remote.dto.RegisterRequest
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario.
 * 
 * Encapsula la lógica de registro de usuarios nuevos en el sistema.
 * Construye el RegisterRequest con los datos del usuario y lo envía
 * al repositorio.
 * 
 * Flujo de registro:
 * 1. Usuario completa el formulario de registro
 * 2. Este use case crea el RegisterRequest
 * 3. El repositorio envía la petición al servidor
 * 4. El servidor envía un código de verificación por email
 * 5. Usuario debe verificar el código (ver VerifyRegisterUseCase)
 * 
 * @param repository Repositorio de autenticación
 * @author Fred Rueda
 * @version 1.0
 */
class RegisterUseCase(
    private val repository: AuthRepository
) {
    /**
     * Ejecuta el caso de uso de registro.
     * 
     * @param email Correo electrónico del nuevo usuario
     * @param password Contraseña del nuevo usuario
     * @param username Nombre de usuario único
     * @param firstName Nombre(s) del usuario
     * @param lastName Apellido(s) del usuario
     * @return RegisterResponse con mensaje y código de verificación (en desarrollo)
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ) = repository.register(
        RegisterRequest(
            email = email,
            password = password,
            username = username,
            first_name = firstName,
            last_name = lastName
        )
    )
}
