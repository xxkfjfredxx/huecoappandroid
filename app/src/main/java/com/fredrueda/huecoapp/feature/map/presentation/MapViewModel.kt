package com.fredrueda.huecoapp.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import com.fredrueda.huecoapp.feature.report.domain.usecase.GetHuecosCercanosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getHuecosCercanosUseCase: GetHuecosCercanosUseCase,
    private val huecoRepository: HuecoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    // 🔹 Cargar huecos cercanos para el mapa
    fun cargarHuecosCercanos(
        latitud: Double,
        longitud: Double,
        radio: Int = 100
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getHuecosCercanosUseCase(latitud, longitud, radio)) {
                is ApiResponse.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        huecos = result.data,
                        error = null
                    )
                }
                is ApiResponse.HttpError -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Error HTTP ${result.code}"
                    )
                }
                is ApiResponse.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.throwable.message ?: "Error de red"
                    )
                }
            }
        }
    }

    // ---------- Selección de hueco para overlay ----------
    fun seleccionarHueco(hueco: HuecoResponse) {
        _uiState.value = _uiState.value.copy(selectedHueco = hueco)
    }

    fun cerrarOverlay() {
        _uiState.value = _uiState.value.copy(selectedHueco = null)
    }

    // ---------- VALIDACIONES (pendiente_validacion) ----------
    fun validarHuecoExiste(huecoId: Int) {
        viewModelScope.launch {
            when (val result = huecoRepository.validarHueco(huecoId, true)) {
                is ApiResponse.Success -> {
                    _uiState.value = _uiState.value.let { state ->
                        val nuevosHuecos = state.huecos.map { h ->
                            if (h.id == huecoId) {
                                val nuevasPos = (h.validacionesPositivas ?: 0) + 1
                                h.copy(
                                    validadoUsuario = true,
                                    validacionesPositivas = nuevasPos,
                                    faltanValidaciones = max(0, 5 - nuevasPos)
                                )
                            } else h
                        }

                        val nuevoSeleccionado = nuevosHuecos.find { it.id == huecoId }

                        state.copy(
                            huecos = nuevosHuecos,
                            selectedHueco = nuevoSeleccionado,
                            mensaje = "¡Gracias por validar este hueco! 🙌"
                        )
                    }
                }
                is ApiResponse.HttpError -> {
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Ya validaste este hueco 👍"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Error al validar"
                        )
                    }
                }
                is ApiResponse.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = "Error de red"
                    )
                }
            }

            // 👀 Importante: NO cierro overlay aquí para que se vea el cambio
            // cerrarOverlay()
        }
    }


    fun validarHuecoNoExiste(huecoId: Int) {
        viewModelScope.launch {
            when (val result = huecoRepository.validarHueco(huecoId, false)) {
                is ApiResponse.Success -> {
                    _uiState.value = _uiState.value.let { state ->
                        val nuevosHuecos = state.huecos.map { h ->
                            if (h.id == huecoId) {
                                val nuevasNeg = (h.validacionesNegativas ?: 0) + 1
                                h.copy(
                                    validadoUsuario = true,
                                    validacionesNegativas = nuevasNeg
                                    // si quisieras, también podrías recalcular faltanValidaciones
                                )
                            } else h
                        }

                        val nuevoSeleccionado = nuevosHuecos.find { it.id == huecoId }

                        state.copy(
                            huecos = nuevosHuecos,
                            selectedHueco = nuevoSeleccionado,
                            mensaje = "Gracias por tu validación 🙌"
                        )
                    }
                }
                is ApiResponse.HttpError -> {
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Ya validaste este hueco 👍"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Error al validar"
                        )
                    }
                }
                is ApiResponse.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        mensaje = "Error de red"
                    )
                }
            }

            // tampoco cierro overlay aquí
            // cerrarOverlay()
        }
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }


    // ---------- CONFIRMACIONES (activo → reparado/abierto/cerrado) ----------
    fun reportarReparado(huecoId: Int) {
        viewModelScope.launch {
            huecoRepository.confirmarHueco(huecoId, confirmado = false)
            cerrarOverlay()
        }
    }

    fun reportarAbierto(huecoId: Int) {
        viewModelScope.launch {
            huecoRepository.confirmarHueco(huecoId, confirmado = true)
            cerrarOverlay()
        }
    }

    fun reportarCerrado(huecoId: Int) {
        viewModelScope.launch {
            huecoRepository.confirmarHueco(huecoId, confirmado = false)
            cerrarOverlay()
        }
    }
}
