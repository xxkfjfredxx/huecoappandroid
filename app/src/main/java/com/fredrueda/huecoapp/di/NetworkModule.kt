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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides @Singleton
    fun provideSessionManager(@ApplicationContext ctx: Context) = SessionManager(ctx)

    @Provides @Singleton
    fun provideOkHttp(
        sessionManager: SessionManager,
        authenticator: AuthAuthenticator
    ): OkHttpClient {
        val log = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(log)
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(authenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)
}
