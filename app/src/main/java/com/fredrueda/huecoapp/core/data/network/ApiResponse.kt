package com.fredrueda.huecoapp.core.data.network

/**
 * Wrapper genérico para respuestas del API.
 * 
 * Encapsula los diferentes tipos de respuestas que puede devolver
 * una petición HTTP:
 * - Success: Petición exitosa con datos
 * - HttpError: Error HTTP (4xx, 5xx)
 * - NetworkError: Error de red o conexión
 * 
 * Uso:
 * ```kotlin
 * when (response) {
 *     is ApiResponse.Success -> // manejar datos
 *     is ApiResponse.HttpError -> // manejar error HTTP
 *     is ApiResponse.NetworkError -> // manejar error de red
 * }
 * ```
 * 
 * @param T Tipo de datos esperados en caso de éxito
 * @author Fred Rueda
 * @version 1.0
 */
sealed class ApiResponse<out T> {
    /**
     * Respuesta exitosa con datos.
     * @property data Datos devueltos por el API
     */
    data class Success<T>(val data: T): ApiResponse<T>()
    
    /**
     * Error HTTP (4xx, 5xx).
     * @property code Código de estado HTTP (400, 401, 404, 500, etc.)
     * @property message Mensaje de error del servidor (opcional)
     */
    data class HttpError(val code: Int, val message: String? = null): ApiResponse<Nothing>()
    
    /**
     * Error de red o conexión.
     * @property throwable Excepción lanzada (IOException, TimeoutException, etc.)
     */
    data class NetworkError(val throwable: Throwable): ApiResponse<Nothing>()
}