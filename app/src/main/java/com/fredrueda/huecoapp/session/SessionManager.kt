package com.fredrueda.huecoapp.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fredrueda.huecoapp.utils.constants.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(AppConstants.DS_NAME)

class SessionManager(private val context: Context) {
    // 🔹 Claves públicas (usadas también por SessionViewModel)
    val KEY_ACCESS = stringPreferencesKey(AppConstants.KEY_ACCESS)
    val KEY_REFRESH = stringPreferencesKey(AppConstants.KEY_REFRESH)

    // 🔹 Flujo completo del DataStore (para observar tokens en tiempo real)
    val dataFlow: Flow<Map<androidx.datastore.preferences.core.Preferences.Key<String>, String>> =
        context.dataStore.data.map { prefs ->
            prefs.asMap().filterKeys { it == KEY_ACCESS || it == KEY_REFRESH } as Map<androidx.datastore.preferences.core.Preferences.Key<String>, String>
        }

    suspend fun saveTokens(access: String?, refresh: String?) {
        context.dataStore.edit { prefs ->
            access?.let { prefs[KEY_ACCESS] = it }
            refresh?.let { prefs[KEY_REFRESH] = it }
        }
    }

    suspend fun getAccess(): String? =
        context.dataStore.data.map { it[KEY_ACCESS] }.first()

    suspend fun getRefresh(): String? =
        context.dataStore.data.map { it[KEY_REFRESH] }.first()

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
