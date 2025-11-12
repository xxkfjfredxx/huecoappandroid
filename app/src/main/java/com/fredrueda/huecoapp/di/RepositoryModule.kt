package com.fredrueda.huecoapp.di

import com.fredrueda.huecoapp.feature.auth.data.repository.AuthRepositoryImpl
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
