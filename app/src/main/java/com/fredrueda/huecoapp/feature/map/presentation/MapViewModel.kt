package com.fredrueda.huecoapp.feature.map.presentation

import android.util.Log
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
        Log.d("MapViewModel", "seleccionarHueco: id=${hueco.id} miConfirmacion=${hueco.miConfirmacion}")
        _uiState.value = _uiState.value.copy(selectedHueco = hueco)
    }

    fun cerrarOverlay() {
        _uiState.value = _uiState.value.copy(selectedHueco = null)
    }

    // ---------- VALIDACIONES (pendiente_validacion) ----------
    fun validarHuecoExiste(huecoId: Int) {
        viewModelScope.launch {
            // Optimistic update: marcar localmente que ya validó para mejorar la UX inmediata
            val optimisticState = _uiState.value.let { state ->
                val nuevos = state.huecos.map { h ->
                    if (h.id == huecoId) h.copy(
                        miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                            id = null,
                            hueco = huecoId,
                            usuario = null,
                            usuarioNombre = null,
                            confirmado = null,
                            fecha = null,
                            nuevoEstado = null
                        ),
                        validadoUsuario = true
                    ) else h
                }
                state.copy(huecos = nuevos)
            }
            _uiState.value = optimisticState

            when (val result = huecoRepository.validarHueco(huecoId, true)) {
                is ApiResponse.Success -> {
                    val resp = result.data
                    _uiState.value = _uiState.value.let { state ->
                        // incrementar contador positivo
                        val nuevosHuecos = state.huecos.map { h ->
                            if (h.id == huecoId) {
                                val nuevasPos = (h.validacionesPositivas ?: 0) + 1
                                h.copy(
                                    validacionesPositivas = nuevasPos,
                                    faltanValidaciones = max(0, 5 - nuevasPos)
                                )
                            } else h
                        }
                        // además actualizar miConfirmacion indicando que este usuario votó positivo
                        val nuevosHuecosWithConf = nuevosHuecos.map { h ->
                            if (h.id == huecoId) h.copy(
                                miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                                    id = resp.id,
                                    hueco = resp.hueco,
                                    usuario = resp.usuario,
                                    usuarioNombre = resp.usuarioNombre,
                                    confirmado = null,
                                    fecha = resp.fecha,
                                    nuevoEstado = resp.nuevoEstado
                                ),
                                validadoUsuario = true
                            ) else h
                        }
                        val nuevoSeleccionado = nuevosHuecosWithConf.find { it.id == huecoId }
                        // Log para depuración: mostrar miConfirmacion y validadoUsuario tras actualizar
                        Log.d("MapViewModel", "validarHuecoExiste success resp=$resp nuevoSeleccionado.miConfirmacion=${nuevoSeleccionado?.miConfirmacion} validadoUsuario=${nuevoSeleccionado?.validadoUsuario}")
                        state.copy(
                            huecos = nuevosHuecosWithConf,
                            selectedHueco = nuevoSeleccionado,
                            mensaje = "¡Gracias por validar este hueco! 🙌",
                            closeInfoWindow = true,
                            reopenInfoWindowId = huecoId // activa reapertura
                        )
                    }
                }
                is ApiResponse.HttpError -> {
                    // Revertir optimistic update si fue un error no esperado
                    if (result.message?.contains("Ya has validado este hueco") != true) {
                        // Revertir: quitar miConfirmacion.voto para ese hueco
                        _uiState.value = _uiState.value.copy(
                            huecos = _uiState.value.huecos.map { h -> if (h.id == huecoId) h.copy(miConfirmacion = null, validadoUsuario = null) else h }
                        )
                    }
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Ya validaste este hueco 👍",
                            closeInfoWindow = true // también cierra en error conocido
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Error al validar"
                        )
                    }
                }
                is ApiResponse.NetworkError -> {
                    // Revertir optimistic update en caso de fallo de red
                    _uiState.value = _uiState.value.copy(
                        huecos = _uiState.value.huecos.map { h -> if (h.id == huecoId) h.copy(miConfirmacion = null, validadoUsuario = null) else h },
                        mensaje = "Error de red"
                    )
                }
            }
        }
    }

    fun validarHuecoNoExiste(huecoId: Int) {
        viewModelScope.launch {
            // Optimistic update: marcar localmente voto negativo
            _uiState.value = _uiState.value.copy(
                huecos = _uiState.value.huecos.map { h -> if (h.id == huecoId) h.copy(miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                    id = null, hueco = huecoId, usuario = null, usuarioNombre = null, confirmado = null, fecha = null, nuevoEstado = null
                ), validadoUsuario = true) else h }
            )

            when (val result = huecoRepository.validarHueco(huecoId, false)) {
                is ApiResponse.Success -> {
                    val resp = result.data
                    _uiState.value = _uiState.value.let { state ->
                        val nuevosHuecos = state.huecos.map { h ->
                            if (h.id == huecoId) {
                                val nuevasNeg = (h.validacionesNegativas ?: 0) + 1
                                h.copy(
                                    validacionesNegativas = nuevasNeg
                                )
                            } else h
                        }
                        val nuevosHuecosWithConf = nuevosHuecos.map { h ->
                            if (h.id == huecoId) h.copy(
                                miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                                    id = resp.id,
                                    hueco = resp.hueco,
                                    usuario = resp.usuario,
                                    usuarioNombre = resp.usuarioNombre,
                                    confirmado = null,
                                    fecha = resp.fecha,
                                    nuevoEstado = resp.nuevoEstado
                                ),
                                validadoUsuario = true
                            ) else h
                        }
                        val nuevoSeleccionado = nuevosHuecosWithConf.find { it.id == huecoId }
                        Log.d("MapViewModel", "validarHuecoNoExiste success resp=$resp nuevoSeleccionado.miConfirmacion=${nuevoSeleccionado?.miConfirmacion} validadoUsuario=${nuevoSeleccionado?.validadoUsuario}")
                        state.copy(
                            huecos = nuevosHuecosWithConf,
                            selectedHueco = nuevoSeleccionado,
                            mensaje = "Gracias por tu validación 🙌",
                            closeInfoWindow = true, // activa bandera para cerrar InfoWindow
                            reopenInfoWindowId = huecoId // <-- AÑADIDO para reabrir
                        )
                    }
                }
                is ApiResponse.HttpError -> {
                    // Revertir optimistic update si no es un mensaje esperado
                    if (result.message?.contains("Ya has validado este hueco") != true) {
                        _uiState.value = _uiState.value.copy(
                            huecos = _uiState.value.huecos.map { h -> if (h.id == huecoId) h.copy(miConfirmacion = null, validadoUsuario = null) else h }
                        )
                    }
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Ya validaste este hueco 👍",
                            closeInfoWindow = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            mensaje = "Error al validar"
                        )
                    }
                }
                is ApiResponse.NetworkError -> {
                    // Revertir optimistic update
                    _uiState.value = _uiState.value.copy(
                        huecos = _uiState.value.huecos.map { h -> if (h.id == huecoId) h.copy(miConfirmacion = null, validadoUsuario = null) else h },
                        mensaje = "Error de red"
                    )
                }
            }
        }
    }

    fun infoWindowCerrado() {
        _uiState.value = _uiState.value.copy(closeInfoWindow = false)
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }


    // ---------- CONFIRMACIONES (activo → reparado/abierto/cerrado) ----------
    fun reportarReparado(huecoId: Int) {
        viewModelScope.launch {
            when (val res = huecoRepository.confirmarHueco(huecoId, 7)) { // 7 = Reparado
                is ApiResponse.Success -> {
                    val conf = res.data
                    // actualizar huecos en estado
                    _uiState.value = _uiState.value.let { state ->
                        // No cambiar `estado` aquí: solo actualizar la confirmación del usuario
                        val nuevosHuecosWithConf = state.huecos.map { h ->
                            if (h.id == huecoId) h.copy(miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                                id = conf.id,
                                hueco = conf.hueco,
                                usuario = conf.usuario,
                                usuarioNombre = conf.usuarioNombre,
                                confirmado = null,
                                fecha = conf.fecha,
                                nuevoEstado = conf.nuevoEstado
                            )) else h
                        }
                        val nuevoSel = nuevosHuecosWithConf.find { it.id == huecoId }
                        Log.d("MapViewModel", "reportarReparado: huecoId=$huecoId nuevoEstado=${conf.nuevoEstado}")
                        state.copy(huecos = nuevosHuecosWithConf, selectedHueco = nuevoSel, mensaje = "Estado actualizado", closeInfoWindow = true, reopenInfoWindowId = huecoId)
                     }
                }
                is ApiResponse.HttpError -> _uiState.value = _uiState.value.copy(mensaje = "Error al actualizar estado")
                is ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(mensaje = "Error de red")
            }
            cerrarOverlay()
        }
    }

    fun reportarAbierto(huecoId: Int) {
        viewModelScope.launch {
            when (val res = huecoRepository.confirmarHueco(huecoId, 6)) { // 6 = En Reparación (según mapping?)
                is ApiResponse.Success -> {
                    val conf = res.data
                    _uiState.value = _uiState.value.let { state ->
                        // Solo actualizar miConfirmacion sin tocar el estado
                        val nuevosHuecosWithConf = state.huecos.map { h ->
                            if (h.id == huecoId) h.copy(miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                                id = conf.id,
                                hueco = conf.hueco,
                                usuario = conf.usuario,
                                usuarioNombre = conf.usuarioNombre,
                                confirmado = null,
                                fecha = conf.fecha,
                                nuevoEstado = conf.nuevoEstado
                            )) else h
                        }
                        val nuevoSeleccionado = nuevosHuecosWithConf.find { it.id == huecoId }
                        Log.d("MapViewModel", "reportarAbierto: huecoId=$huecoId nuevoEstado=${conf.nuevoEstado}")
                        state.copy(huecos = nuevosHuecosWithConf, selectedHueco = nuevoSeleccionado, mensaje = "Estado actualizado", closeInfoWindow = true, reopenInfoWindowId = huecoId)
                    }
                 }
                 is ApiResponse.HttpError -> _uiState.value = _uiState.value.copy(mensaje = "Error al actualizar estado")
                 is ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(mensaje = "Error de red")
             }
             cerrarOverlay()
         }
     }

    fun reportarCerrado(huecoId: Int) {
        viewModelScope.launch {
            when (val res = huecoRepository.confirmarHueco(huecoId, 5)) { // 5 = Cerrado
                is ApiResponse.Success -> {
                    val conf = res.data
                    _uiState.value = _uiState.value.let { state ->
                        // Solo actualizar miConfirmacion sin tocar el estado
                        val nuevosHuecosWithConf = state.huecos.map { h ->
                            if (h.id == huecoId) h.copy(miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                                id = conf.id,
                                hueco = conf.hueco,
                                usuario = conf.usuario,
                                usuarioNombre = conf.usuarioNombre,
                                confirmado = null,
                                fecha = conf.fecha,
                                nuevoEstado = conf.nuevoEstado
                            )) else h
                        }
                        val nuevoSeleccionado = nuevosHuecosWithConf.find { it.id == huecoId }
                        Log.d("MapViewModel", "reportarCerrado: huecoId=$huecoId nuevoEstado=${conf.nuevoEstado}")
                        state.copy(huecos = nuevosHuecosWithConf, selectedHueco = nuevoSeleccionado, mensaje = "Estado actualizado", closeInfoWindow = true, reopenInfoWindowId = huecoId)
                    }
                 }
                 is ApiResponse.HttpError -> _uiState.value = _uiState.value.copy(mensaje = "Error al actualizar estado")
                 is ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(mensaje = "Error de red")
             }
             cerrarOverlay()
         }
     }

    private fun mapEstadoIntToString(estado: Int): String {
        return when (estado) {
            1 -> "pendiente_validacion"
            2 -> "activo"
            3 -> "rechazado"
            4 -> "reabierto"
            5 -> "cerrado"
            6 -> "en_reparacion"
            7 -> "reparado"
            else -> ""
        }
    }

    fun reabrirInfoWindow(huecoId: Int) {
        val hueco = _uiState.value.huecos.find { it.id == huecoId }
        if (hueco != null) {
            _uiState.value = _uiState.value.copy(selectedHueco = hueco)
        }
    }

    fun marcarParaReabrirInfoWindow(huecoId: Int) {
        _uiState.value = _uiState.value.copy(reopenInfoWindowId = huecoId)
    }

    fun limpiarReopenInfoWindow() {
        _uiState.value = _uiState.value.copy(reopenInfoWindowId = null)
    }

    fun toggleFollow(huecoId: Int, isFollowed: Boolean) {
        viewModelScope.launch {
            val result = if (isFollowed) {
                huecoRepository.unfollowHueco(huecoId)
            } else {
                huecoRepository.followHueco(huecoId)
            }
            if (result is ApiResponse.Success) {
                _uiState.value = _uiState.value.let { state ->
                    val nuevosHuecos = state.huecos.map { h ->
                        if (h.id == huecoId) h.copy(isFollowed = !isFollowed) else h
                    }
                    val nuevoSeleccionado = nuevosHuecos.find { it.id == huecoId }
                    state.copy(
                        huecos = nuevosHuecos,
                        selectedHueco = nuevoSeleccionado,
                        closeInfoWindow = true,
                        reopenInfoWindowId = huecoId
                    )
                }
            }
        }
    }
}
