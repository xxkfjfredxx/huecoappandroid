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
 * Observa el DataStore de SessionManager.
 * Si los tokens se eliminan (por expiración o logout),
 * emite false → y la UI navega al Login.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isSessionActive = MutableStateFlow<Boolean?>(null)
    val isSessionActive: StateFlow<Boolean?> = _isSessionActive.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.dataFlow.collectLatest { prefs ->
                val access = prefs[sessionManager.KEY_ACCESS]
                _isSessionActive.value = !access.isNullOrEmpty()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clear()
            _isSessionActive.value = false
        }
    }
}
