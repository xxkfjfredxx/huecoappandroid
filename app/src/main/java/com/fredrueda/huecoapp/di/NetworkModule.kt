package com.fredrueda.huecoapp.di

import android.content.Context
import com.fredrueda.huecoapp.feature.auth.data.remote.api.AuthApi
import com.fredrueda.huecoapp.session.AuthAuthenticator
import com.fredrueda.huecoapp.session.AuthInterceptor
import com.fredrueda.huecoapp.session.SessionManager
import com.fredrueda.huecoapp.utils.constants.AppConstants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo de Hilt para la inyección de dependencias de red.
 * 
 * Proporciona instancias únicas (Singleton) de:
 * - Retrofit: Cliente HTTP para consumir APIs REST
 * - OkHttpClient: Cliente HTTP con interceptores personalizados
 * - Gson: Conversor JSON
 * - SessionManager: Gestor de sesión y tokens
 * - APIs: Interfaces de Retrofit para endpoints
 * 
 * @author Fred Rueda
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Proporciona una instancia de Gson configurada.
     * setLenient() permite parsear JSON no estrictamente válido.
     * 
     * @return Instancia de Gson
     */
    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    /**
     * Proporciona el gestor de sesión para almacenar tokens.
     * 
     * @param ctx Contexto de la aplicación
     * @return Instancia de SessionManager
     */
    @Provides @Singleton
    fun provideSessionManager(@ApplicationContext ctx: Context) = SessionManager(ctx)

    /**
     * Proporciona un cliente OkHttp configurado con:
     * - Logging interceptor: Para ver logs de peticiones/respuestas
     * - Auth interceptor: Agrega automáticamente el token de acceso a las peticiones
     * - Authenticator: Refresca el token cuando expira (401)
     * - Timeouts: 30 segundos para conexión y lectura
     * 
     * @param sessionManager Gestor de sesión
     * @param authenticator Autenticador para refrescar tokens
     * @return Cliente OkHttp configurado
     */
    @Provides @Singleton
    fun provideOkHttp(
        sessionManager: SessionManager,
        authenticator: AuthAuthenticator
    ): OkHttpClient {
        // Interceptor para logging de peticiones HTTP (solo en debug)
        val log = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(log) // Logs de peticiones/respuestas
            .addInterceptor(AuthInterceptor(sessionManager)) // Agrega token de acceso
            .authenticator(authenticator) // Refresca token automáticamente
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión
            .readTimeout(30, TimeUnit.SECONDS) // Timeout de lectura
            .build()
    }

    /**
     * Proporciona una instancia de Retrofit configurada.
     * 
     * @param gson Conversor JSON
     * @param client Cliente OkHttp
     * @return Instancia de Retrofit
     */
    @Provides @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL) // URL base del API
            .client(client) // Cliente HTTP configurado
            .addConverterFactory(GsonConverterFactory.create(gson)) // Conversor JSON
            .build()

    /**
     * Proporciona la interfaz del API de autenticación.
     * 
     * @param retrofit Instancia de Retrofit
     * @return Implementación de AuthApi
     */
    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
