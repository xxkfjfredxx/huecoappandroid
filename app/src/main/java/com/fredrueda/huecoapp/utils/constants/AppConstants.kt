package com.fredrueda.huecoapp.utils.constants

/**
 * Constantes globales de la aplicación.
 * 
 * Contiene URLs del API, endpoints y configuraciones de DataStore.
 * 
 * @author Fred Rueda
 * @version 1.0
 */
object AppConstants {
    
    // ========== CONFIGURACIÓN DEL SERVIDOR ==========
    
    /**
     * URL base del API backend.
     * 
     * NOTA: Ajustar según el entorno:
     * - Emulador Android: usar "http://10.0.2.2:8000/" para localhost
     * - Dispositivo físico: usar la IP local de tu PC (ej: "http://192.168.1.7:8000/")
     * - Producción: usar HTTPS con dominio real
     */
    const val BASE_URL = "http://192.168.1.3:8000/"
    
    // ========== ENDPOINTS DEL API ==========
    
    /** Endpoint de inicio de sesión */
    const val LOGIN = "api/auth/login"
    
    /** Endpoint para refrescar el token de acceso */
    const val REFRESH = "api/auth/refresh"
    
    /** Endpoint para obtener información del usuario autenticado */
    const val ME = "api/auth/me"
    
    /** Endpoint para cerrar sesión */
    const val LOGOUT = "api/auth/logout"

    // ========== CONFIGURACIÓN DE DATASTORE ==========
    
    /** Nombre del archivo DataStore para persistencia de sesión */
    const val DS_NAME = "session_datastore"
    
    /** Clave para almacenar el token de acceso JWT */
    const val KEY_ACCESS = "access_token"
    
    /** Clave para almacenar el token de refresco JWT */
    const val KEY_REFRESH = "refresh_token"
}