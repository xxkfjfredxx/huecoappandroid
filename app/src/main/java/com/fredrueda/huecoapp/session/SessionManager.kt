package com.fredrueda.huecoapp.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fredrueda.huecoapp.utils.constants.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extensión de Context para crear una instancia única de DataStore
private val Context.dataStore by preferencesDataStore(AppConstants.DS_NAME)

/**
 * Gestor de sesión de usuario.
 * 
 * Maneja el almacenamiento persistente y seguro de tokens de autenticación (access y refresh)
 * utilizando Jetpack DataStore Preferences.
 * 
 * Funcionalidades:
 * - Guardar tokens JWT (access y refresh)
 * - Recuperar tokens almacenados
 * - Observar cambios en los tokens en tiempo real
 * - Limpiar sesión (cerrar sesión)
 * 
 * @param context Contexto de la aplicación
 * @author Fred Rueda
 * @version 1.0
 */
class SessionManager(private val context: Context) {
    
    // Claves para almacenar los tokens en DataStore (usadas también por SessionViewModel)
    val KEY_ACCESS = stringPreferencesKey(AppConstants.KEY_ACCESS)
    val KEY_REFRESH = stringPreferencesKey(AppConstants.KEY_REFRESH)

    /**
     * Flujo reactivo que emite los tokens almacenados.
     * Permite observar cambios en los tokens en tiempo real.
     * 
     * @return Flow con mapa de tokens (access y refresh)
     */
    val dataFlow: Flow<Map<androidx.datastore.preferences.core.Preferences.Key<String>, String>> =
        context.dataStore.data.map { prefs ->
            prefs.asMap().filterKeys { it == KEY_ACCESS || it == KEY_REFRESH } as Map<androidx.datastore.preferences.core.Preferences.Key<String>, String>
        }

    /**
     * Guarda los tokens de acceso y refresco en DataStore.
     * 
     * @param access Token de acceso JWT (short-lived)
     * @param refresh Token de refresco JWT (long-lived)
     */
    suspend fun saveTokens(access: String?, refresh: String?) {
        context.dataStore.edit { prefs ->
            access?.let { prefs[KEY_ACCESS] = it }
            refresh?.let { prefs[KEY_REFRESH] = it }
        }
    }

    /**
     * Obtiene el token de acceso almacenado.
     * 
     * @return Token de acceso o null si no existe
     */
    suspend fun getAccess(): String? =
        context.dataStore.data.map { it[KEY_ACCESS] }.first()

    /**
     * Obtiene el token de refresco almacenado.
     * 
     * @return Token de refresco o null si no existe
     */
    suspend fun getRefresh(): String? =
        context.dataStore.data.map { it[KEY_REFRESH] }.first()

    /**
     * Limpia todos los datos almacenados en DataStore.
     * Utilizado al cerrar sesión.
     */
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
