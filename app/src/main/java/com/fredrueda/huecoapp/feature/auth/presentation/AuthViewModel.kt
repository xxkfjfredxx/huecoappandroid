package com.fredrueda.huecoapp.feature.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.domain.use_case.ResetPasswordUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.ForgotPasswordUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginWithFacebookUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.LoginWithGoogleUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.RegisterUseCase
import com.fredrueda.huecoapp.feature.auth.domain.usecase.VerifyRegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
/**
 * ViewModel de autenticación.
 * Orquesta login (email, Google, Facebook), registro y verificación OTP,
 * exponiendo estados para la UI via StateFlow y Compose.
 */
class AuthViewModel @Inject constructor(
    private val loginUC: LoginUseCase,
    private val loginGoogleUC: LoginWithGoogleUseCase,
    private val loginFacebookUC: LoginWithFacebookUseCase,
    private val registerUseCase: RegisterUseCase,
    private val verifyRegisterUseCase: VerifyRegisterUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
): ViewModel() {
    var registerState by mutableStateOf(RegisterState())
        private set
    var resetPasswordState by mutableStateOf(ResetPasswordState())
        private set
    var forgotPasswordState by mutableStateOf(ForgotPasswordState())
        private set
    var verifyRegisterState by mutableStateOf(VerifyRegisterState())
        private set
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

    fun register(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    ) {
        viewModelScope.launch {
            registerState = registerState.copy(isLoading = true)

            try {
                val result = registerUseCase(email, password, username, firstName, lastName)
                registerState = registerState.copy(
                    isLoading = false,
                    isSuccess = true,
                    message = result.detail,
                    devCode = result.dev_code
                )
            } catch (e: Exception) {
                registerState = registerState.copy(
                    isLoading = false,
                    message = e.message
                )
            }
        }
    }

    fun verifyRegister(email: String, code: String) {
        viewModelScope.launch {
            verifyRegisterState = verifyRegisterState.copy(isLoading = true)

            when (val r = verifyRegisterUseCase(email, code)) {
                is ApiResponse.Success -> {
                    _state.value = AuthUiState(user = r.data)

                    verifyRegisterState = verifyRegisterState.copy(
                        isLoading = false,
                        isVerified = true,
                        error = null
                    )
                }
                is ApiResponse.HttpError -> {
                    verifyRegisterState = verifyRegisterState.copy(
                        isLoading = false,
                        error = r.message ?: "Error ${r.code}"
                    )
                }
                is ApiResponse.NetworkError -> {
                    verifyRegisterState = verifyRegisterState.copy(
                        isLoading = false,
                        error = r.throwable.message ?: "Network error"
                    )
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            forgotPasswordState = forgotPasswordState.copy(
                isLoading = true,
                error = null,
                message = null
            )

            try {
                val result = forgotPasswordUseCase(email)
                forgotPasswordState = forgotPasswordState.copy(
                    isLoading = false,
                    message = result.detail,
                    error = null
                )
            } catch (e: Exception) {
                forgotPasswordState = forgotPasswordState.copy(
                    isLoading = false,
                    error = e.message ?: "Error al enviar el correo de recuperación"
                )
            }
        }
    }

    fun resetPassword(uid: String, token: String, newPassword: String) {
        viewModelScope.launch {
            // Estado inicial: cargando, limpiamos error anterior
            resetPasswordState = resetPasswordState.copy(
                isLoading = true,
                error = null,
                message = null,
                isSuccess = false
            )

            try {
                val result = resetPasswordUseCase(uid, token, newPassword)
                    resetPasswordState = resetPasswordState.copy(
                        isLoading = false,
                        isSuccess = true,
                        message = result.detail,
                        error = null
                    )

            } catch (e: Exception) {
                resetPasswordState = resetPasswordState.copy(
                    isLoading = false,
                    isSuccess = false,
                    error = e.message ?: "Error al cambiar la contraseña"
                )
            }
        }
    }


}
