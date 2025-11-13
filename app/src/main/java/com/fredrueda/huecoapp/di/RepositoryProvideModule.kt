package com.fredrueda.huecoapp.di

import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import com.fredrueda.huecoapp.feature.auth.domain.usecase.RegisterUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.VerifyRegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvideModule {

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        repository: AuthRepository
    ): RegisterUseCase = RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideVerifyRegisterUseCase(
        repository: AuthRepository
    ): VerifyRegisterUseCase = VerifyRegisterUseCase(repository)
}
