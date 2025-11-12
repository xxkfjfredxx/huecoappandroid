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
 * Authenticator: intercepta respuestas 401 y trata de renovar el token automáticamente.
 * Si no puede, limpia la sesión → usuario deberá volver a iniciar sesión.
 */
class AuthAuthenticator @Inject constructor(
    private val session: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar loops infinitos
        if (response.request.header("Authorization").isNullOrBlank()) return null

        val refresh = runBlocking { session.getRefresh() } ?: return null

        // 🚫 Previene bucles de autenticación
        if (responseCount(response) >= 2) {
            runBlocking { session.clear() } // limpia sesión en caso de fallo repetido
            return null
        }

        return try {
            // 🔹 Llamada sin interceptores (directa) para evitar recursion
            val client = OkHttpClient()
            val body = JSONObject(mapOf("refresh" to refresh)).toString()
                .toRequestBody("application/json".toMediaType())
            val req = Request.Builder()
                .url(AppConstants.BASE_URL + AppConstants.REFRESH)
                .post(body)
                .build()

            // 🔹 Ejecuta llamada de refresh
            val newAccess = client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    // ❌ refresh también falló → limpiamos sesión
                    runBlocking { session.clear() }
                    null
                } else {
                    val json = JSONObject(resp.body?.string() ?: "{}")
                    json.optString("access", null)
                }
            } ?: return null

            // 🔹 Guarda nuevo access token
            runBlocking { session.saveTokens(newAccess, refresh) }

            // 🔹 Reintenta la petición original con nuevo token
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()

        } catch (e: IOException) {
            // ❌ Error de red → sesión expirada o sin conexión
            runBlocking { session.clear() }
            null
        } catch (e: Exception) {
            // ❌ Otro error no controlado → limpia sesión
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
