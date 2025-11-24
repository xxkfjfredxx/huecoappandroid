package com.fredrueda.huecoapp.di

import com.fredrueda.huecoapp.feature.auth.data.repository.AuthRepositoryImpl
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para vincular interfaces de repositorio con sus implementaciones.
 * 
 * Utiliza @Binds para crear una relación entre una interfaz y su implementación,
 * permitiendo que Hilt inyecte la implementación cuando se solicita la interfaz.
 * 
 * Ventajas de @Binds:
 * - Más eficiente que @Provides (no requiere instanciación manual)
 * - Menos código boilerplate
 * - Mejor para abstracciones simples
 * 
 * @author Fred Rueda
 * @version 1.0
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindModule {

    /**
     * Vincula la interfaz AuthRepository con su implementación AuthRepositoryImpl.
     * 
     * Cuando una clase solicita AuthRepository, Hilt inyectará automáticamente
     * una instancia de AuthRepositoryImpl.
     * 
     * @param impl Implementación concreta del repositorio
     * @return Interfaz del repositorio
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
