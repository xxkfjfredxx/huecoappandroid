package com.fredrueda.huecoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase principal de la aplicación HuecoApp.
 * 
 * Esta clase extiende Application y está anotada con @HiltAndroidApp para habilitar
 * la inyección de dependencias con Hilt en toda la aplicación.
 * 
 * @author Fred Rueda
 * @version 1.0
 */
@HiltAndroidApp
class HueApp : Application() {
    /**
     * Método llamado cuando la aplicación se crea.
     * Se ejecuta antes que cualquier Activity, Service u otro componente.
     */
    override fun onCreate() {
        super.onCreate()
        // Inicialización controlada del SDK de Facebook para evitar crash cuando no hay tokens reales.
        try {
            val appId = getString(R.string.facebook_app_id)
            val clientToken = getString(R.string.facebook_client_token)
            val placeholders = setOf(getString(R.string.facebook_app_id), getString(R.string.facebook_client_token))
            val hasValidConfig = appId.isNotBlank() && clientToken.isNotBlank() && !placeholders.contains(appId) && !placeholders.contains(clientToken)
            if (hasValidConfig) {
                com.facebook.FacebookSdk.setClientToken(clientToken)
                com.facebook.FacebookSdk.sdkInitialize(this)
                // Opcional: eventos
                // com.facebook.appevents.AppEventsLogger.activateApp(this)
            }
        } catch (_: Exception) {
            // Silencia errores de inicialización si recursos no existen aún
        }
    }
}
