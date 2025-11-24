package com.fredrueda.huecoapp.di

import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import com.fredrueda.huecoapp.feature.auth.domain.usecase.RegisterUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.VerifyRegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer casos de uso que requieren lógica de creación.
 * 
 * Utiliza @Provides para instanciar manualmente los casos de uso,
 * lo cual es necesario cuando:
 * - El caso de uso no tiene @Inject en su constructor
 * - Se requiere lógica adicional al crear la instancia
 * - Se necesita control sobre cómo se construye el objeto
 * 
 * @author Fred Rueda
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvideModule {

    /**
     * Provee una instancia de RegisterUseCase.
     * 
     * @param repository Repositorio de autenticación (inyectado por Hilt)
     * @return Instancia de RegisterUseCase
     */
    @Provides
    @Singleton
    fun provideRegisterUseCase(
        repository: AuthRepository
    ): RegisterUseCase = RegisterUseCase(repository)

    /**
     * Provee una instancia de VerifyRegisterUseCase.
     * 
     * @param repository Repositorio de autenticación (inyectado por Hilt)
     * @return Instancia de VerifyRegisterUseCase
     */
    @Provides
    @Singleton
    fun provideVerifyRegisterUseCase(
        repository: AuthRepository
    ): VerifyRegisterUseCase = VerifyRegisterUseCase(repository)
}
