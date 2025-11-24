package com.fredrueda.huecoapp.session

import com.fredrueda.huecoapp.utils.constants.AppConstants
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import java.io.IOException

/**
 * Autenticador automático de tokens JWT.
 * 
 * Se ejecuta cuando el servidor devuelve un código 401 (Unauthorized),
 * lo que indica que el Access Token ha expirado.
 * 
 * Funcionalidades:
 * - Intercepta respuestas 401 automáticamente
 * - Intenta renovar el Access Token usando el Refresh Token
 * - Si tiene éxito, reintenta la petición original con el nuevo token
 * - Si falla, limpia la sesión y el usuario debe volver a iniciar sesión
 * - Previene bucles infinitos de autenticación
 * 
 * @param session Gestor de sesión para acceder y guardar tokens
 * @author Fred Rueda
 * @version 1.0
 */
class AuthAuthenticator @Inject constructor(
    private val session: SessionManager
) : Authenticator {

    /**
     * Autentica una petición que falló con 401.
     * 
     * Flujo:
     * 1. Verifica que no sea un bucle infinito (máximo 2 intentos)
     * 2. Obtiene el Refresh Token del SessionManager
     * 3. Hace una petición directa al endpoint /refresh (sin interceptores)
     * 4. Si obtiene un nuevo Access Token, lo guarda y reintenta la petición original
     * 5. Si falla, limpia la sesión completa
     * 
     * @param route Ruta de la petición (no se usa)
     * @param response Respuesta 401 del servidor
     * @return Nueva petición con token actualizado, o null si falló
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar loops infinitos - si no tiene header Authorization, no intentar autenticar
        if (response.request.header("Authorization").isNullOrBlank()) return null

        // Obtiene el Refresh Token
        val refresh = runBlocking { session.getRefresh() } ?: return null

        // Previene bucles de autenticación (máximo 2 intentos)
        if (responseCount(response) >= 2) {
            runBlocking { session.clear() } // limpia sesión en caso de fallo repetido
            return null
        }

        return try {
            // Llamada sin interceptores (directa) para evitar recursión
            val client = OkHttpClient()
            val body = JSONObject(mapOf("refresh" to refresh)).toString()
                .toRequestBody("application/json".toMediaType())
            val req = Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.REFRESH)
                .post(body)
                .build()

            // Ejecuta llamada de refresh al servidor
            val newAccess = client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    // Refresh también falló → limpiamos sesión
                    runBlocking { session.clear() }
                    null
                } else {
                    val json = JSONObject(resp.body?.string() ?: "{}")
                    json.optString("access", null)
                }
            } ?: return null

            // Guarda nuevo Access Token (mantiene el mismo Refresh Token)
            runBlocking { session.saveTokens(newAccess, refresh) }

            // Reintenta la petición original con el nuevo token
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()

        } catch (e: IOException) {
            // Error de red → sesión expirada o sin conexión
            runBlocking { session.clear() }
            null
        } catch (e: Exception) {
            // Otro error no controlado → limpia sesión
            runBlocking { session.clear() }
            null
        }
    }

    /**
     * Cuenta cuántas veces la misma request falló con 401
     * para evitar bucles infinitos en autenticación.
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}
