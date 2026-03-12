package com.fredrueda.huecoapp.feature.report.presentation

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.report.domain.use_case.CreateHuecoUseCase
import com.fredrueda.huecoapp.feature.report.domain.usecase.ReportarHuecoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val createHuecoUseCase: CreateHuecoUseCase,
    private val reportarHuecoUseCase: ReportarHuecoUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ReportUiState())
    val state: StateFlow<ReportUiState> = _state

    fun crearHueco(
        latitud: Double,
        longitud: Double,
        userLat: Double?,
        userLon: Double?,
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
            when (val r = createHuecoUseCase(latitud, longitud, userLat, userLon, descripcion, imagen)) {

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

    fun reportarHueco(huecoId: Int, motivo: String, comentario: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            when (val r = reportarHuecoUseCase(huecoId, motivo, comentario)) {
                is ApiResponse.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        success = true,
                        error = null
                    )
                }
                is ApiResponse.HttpError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = r.message ?: "Error al reportar"
                    )
                }
                is ApiResponse.NetworkError -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Error de conexión"
                    )
                }
            }
        }
    }

    fun actualizarDireccion(context: Context, latitud: Double, longitud: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitud, longitud, 1)
                
                withContext(Dispatchers.Main) {
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val calle = addr.thoroughfare ?: ""
                        val numero = addr.subThoroughfare ?: ""
                        val ciudad = addr.locality ?: ""
                        
                        val direccionFormateada = when {
                            calle.isNotEmpty() -> "$calle $numero, $ciudad".trim()
                            else -> addr.getAddressLine(0) ?: "Dirección sin nombre"
                        }
                        _state.value = _state.value.copy(direccion = direccionFormateada)
                    } else {
                        _state.value = _state.value.copy(direccion = "Dirección no encontrada")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(direccion = "Ubicación detectada (Sin nombre de calle)")
                }
            }
        }
    }
}
