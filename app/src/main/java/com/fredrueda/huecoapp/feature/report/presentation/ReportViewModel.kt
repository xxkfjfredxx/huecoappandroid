package com.fredrueda.huecoapp.feature.report.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.domain.use_case.CreateHuecoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val createHuecoUseCase: CreateHuecoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ReportUiState())
    val state: StateFlow<ReportUiState> = _state

    fun crearHueco(
        latitud: Double,
        longitud: Double,
        descripcion: String,
        imagen: File?
    ) {
        // Estado inicial: cargando, limpiando error
        _state.value = _state.value.copy(
            isLoading = true,
            error = null,
            success = false
        )

        viewModelScope.launch {
            when (val r = createHuecoUseCase(latitud, longitud, descripcion, imagen)) {

                is ApiResponse.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        hueco = r.data,
                        error = null
                    )
                }

                is ApiResponse.HttpError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = false,
                        hueco = null,
                        error = r.message ?: "Error ${r.code}"
                    )
                }

                is ApiResponse.NetworkError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = false,
                        hueco = null,
                        error = r.throwable.message ?: "Network error"
                    )
                }
            }
        }
    }

}
