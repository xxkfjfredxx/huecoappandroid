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
        // Inicialización del SDK de Facebook (descomentado cuando se configure)
        //com.facebook.FacebookSdk.sdkInitialize(this)
        //com.facebook.appevents.AppEventsLogger.activateApp(this)
    }
}
