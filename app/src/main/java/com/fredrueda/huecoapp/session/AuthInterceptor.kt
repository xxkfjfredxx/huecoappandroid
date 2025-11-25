package com.fredrueda.huecoapp.session

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de autenticación para OkHttp.
 * 
 * Intercepta todas las peticiones HTTP salientes y agrega automáticamente
 * el token de acceso JWT en el header "Authorization" si existe.
 * 
 * Este interceptor se ejecuta ANTES de enviar cada petición al servidor,
 * asegurando que todas las peticiones autenticadas incluyan el token.
 * 
 * @param session Gestor de sesión para obtener el token de acceso
 * @author Fred Rueda
 * @version 1.0
 */
class AuthInterceptor(private val session: SessionManager): Interceptor {
    
    /**
     * Intercepta la petición y agrega el header de autorización.
     * 
     * Flujo:
     * 1. Obtiene el token de acceso del SessionManager
     * 2. Si existe, agrega el header "Authorization: Bearer {token}"
     * 3. Procede con la petición (modificada o no)
     * 
     * @param chain Cadena de interceptores de OkHttp
     * @return Respuesta del servidor
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        // Obtiene el token de acceso (bloqueante, ya que Interceptor no soporta suspend)
        val access = runBlocking { session.getAccess() }
        
        // Si hay token, lo agrega al header; sino, envía la petición sin modificar
        val newReq = if (!access.isNullOrBlank()) {
            req.newBuilder()
                .addHeader("Authorization", "Bearer $access")
                .build()
        } else req
        
        return chain.proceed(newReq)
    }
}
