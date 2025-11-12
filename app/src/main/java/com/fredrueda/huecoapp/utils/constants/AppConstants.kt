package com.fredrueda.huecoapp.utils.constants

object  AppConstants {
    // Ajusta si usas https / dominio real
    const val BASE_URL = "http://192.168.1.7:8000/" // emulador Android → localhost backend
    const val LOGIN = "api/auth/login"
    const val REFRESH = "api/auth/refresh"
    const val ME = "api/auth/me"
    const val LOGOUT = "api/auth/logout"

    // DataStore
    const val DS_NAME = "session_datastore"
    const val KEY_ACCESS = "access_token"
    const val KEY_REFRESH = "refresh_token"
}