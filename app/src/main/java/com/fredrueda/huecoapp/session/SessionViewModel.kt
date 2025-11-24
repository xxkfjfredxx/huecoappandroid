package com.fredrueda.huecoapp.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel que observa el estado de la sesión del usuario.
 * 
 * Monitorea los cambios en el DataStore (SessionManager) y notifica a la UI
 * cuando la sesión se activa o desactiva.
 * 
 * Funcionalidades:
 * - Observa los tokens en tiempo real
 * - Emite true si hay sesión activa (token válido)
 * - Emite false si la sesión expiró o se cerró
 * - Permite cerrar sesión (logout)
 * 
 * La UI puede observar [isSessionActive] para navegar automáticamente
 * al Login cuando la sesión se pierde.
 * 
 * @param sessionManager Gestor de sesión
 * @author Fred Rueda
 * @version 1.0
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    /**
     * Estado de la sesión del usuario.
     * - true: Sesión activa (hay token de acceso)
     * - false: Sesión cerrada o expirada
     * - null: Estado inicial (aún no se ha verificado)
     */
    private val _isSessionActive = MutableStateFlow<Boolean?>(null)
    val isSessionActive: StateFlow<Boolean?> = _isSessionActive.asStateFlow()

    init {
        observeSession()
    }

    /**
     * Observa el DataStore para detectar cambios en los tokens.
     * 
     * Se ejecuta automáticamente al crear el ViewModel y observa
     * continuamente los cambios en el Access Token.
     * 
     * Si el token desaparece (logout o expiración), emite false.
     */
    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.dataFlow.collectLatest { prefs ->
                val access = prefs[sessionManager.KEY_ACCESS]
                _isSessionActive.value = !access.isNullOrEmpty()
            }
        }
    }

    /**
     * Cierra la sesión del usuario.
     * 
     * Limpia todos los tokens almacenados en DataStore y emite false
     * en [isSessionActive], lo que debería provocar que la UI navegue al Login.
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.clear()
            _isSessionActive.value = false
        }
    }
}
