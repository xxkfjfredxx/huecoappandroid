package com.fredrueda.huecoapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: StateFlow<AuthUser?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        viewModelScope.launch {
            when (val res = authRepository.me()) {
                is ApiResponse.Success -> {
                    _user.value = res.data
                    _error.value = null
                }
                is ApiResponse.HttpError -> {
                    _error.value = res.message ?: "Error HTTP ${res.code}"
                }
                is ApiResponse.NetworkError -> {
                    _error.value = res.throwable.message ?: "Error de red"
                }
            }
        }
    }
}
