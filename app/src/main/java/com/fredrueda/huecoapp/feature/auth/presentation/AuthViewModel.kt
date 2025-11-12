package com.fredrueda.huecoapp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginWithFacebookUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUC: LoginUseCase,
    private val loginGoogleUC: LoginWithGoogleUseCase,
    private val loginFacebookUC: LoginWithFacebookUseCase
): ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun login(email: String, password: String) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = loginUC(email, password)) {
                is ApiResponse.Success -> _state.value = AuthUiState(user = r.data)
                is ApiResponse.HttpError -> _state.value = AuthUiState(error = r.message ?: "Error ${r.code}")
                is ApiResponse.NetworkError -> _state.value = AuthUiState(error = r.throwable.message ?: "Network error")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = loginGoogleUC(idToken)) {
                is ApiResponse.Success -> _state.value = AuthUiState(user = r.data)
                is ApiResponse.HttpError -> _state.value = AuthUiState(error = r.message ?: "Error ${r.code}")
                is ApiResponse.NetworkError -> _state.value = AuthUiState(error = r.throwable.message ?: "Network error")
            }
        }
    }

    fun loginWithFacebook(accessToken: String) {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = loginFacebookUC(accessToken)) {
                is ApiResponse.Success -> _state.value = AuthUiState(user = r.data)
                is ApiResponse.HttpError -> _state.value = AuthUiState(error = r.message ?: "Error ${r.code}")
                is ApiResponse.NetworkError -> _state.value = AuthUiState(error = r.throwable.message ?: "Network error")
            }
        }
    }
}
