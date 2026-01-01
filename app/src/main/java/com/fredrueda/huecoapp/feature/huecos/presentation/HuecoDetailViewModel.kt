package com.fredrueda.huecoapp.feature.huecos.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.feature.huecos.data.repository.HuecoDetailRepository
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.CreateComentarioRequest
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HuecoDetailViewModel @Inject constructor(
    private val repository: HuecoDetailRepository,
    private val huecoRepository: HuecoRepository
) : ViewModel() {
    private val _huecoDetail = MutableStateFlow<HuecoResponse?>(null)
    val huecoDetail: StateFlow<HuecoResponse?> = _huecoDetail

    private val _comentarios = MutableStateFlow<List<ComentarioResponse>>(emptyList())
    val comentarios: StateFlow<List<ComentarioResponse>> = _comentarios

    // Selección local de validación: 1 = sí existe, 0 = no existe, null = sin selección
    private val _selectedValidation = MutableStateFlow<Int?>(null)
    val selectedValidation: StateFlow<Int?> = _selectedValidation

    // Selección local para confirmaciones de estado (ej. 5,6,7)
    private val _selectedConfirmation = MutableStateFlow<Int?>(null)
    val selectedConfirmation: StateFlow<Int?> = _selectedConfirmation

    // Nuevo: total de comentarios (null si desconocido)
    private val _comentariosCount = MutableStateFlow<Int?>(null)
    val comentariosCount: StateFlow<Int?> = _comentariosCount

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    private var isPostingComentario = false
    private val _isConfirming = MutableStateFlow(false)
    val isConfirming: StateFlow<Boolean> = _isConfirming

    fun initializeWith(hueco: HuecoResponse) {
        // Actualizar selección de confirmación siempre con lo que venga al navegar
        _selectedConfirmation.value = hueco.miConfirmacion?.nuevoEstado
        Log.d("HuecoDetailVM", "initializeWith: miConfirmacion=${hueco.miConfirmacion}")

        // Solo setear el detalle completo si aún no lo tenemos; evitar sobreescribir
        // un detalle ya cargado desde servidor.
        if (_huecoDetail.value == null) {
            _huecoDetail.value = hueco
            // Si el hueco trae comentarios, inicializamos la lista local (tomamos hasta 3)
            val initial = hueco.comentarios ?: emptyList()
            if (initial.isNotEmpty() && _comentarios.value.isEmpty()) {
                // ordenar por fecha descendente (más recientes primero) y tomar hasta 3
                _comentarios.value = initial.sortedByDescending { it.fecha ?: "" }.take(3)
            }
            // Si el hueco trae un listado, usaremos su tamaño como conteo conocido
            if (hueco.totalComentarios != null) {
                _comentariosCount.value = hueco.totalComentarios
            } else if (!hueco.comentarios.isNullOrEmpty()) {
                _comentariosCount.value = hueco.comentarios.size
            }
        }
    }

    fun loadHuecoDetail(id: Int) {
        viewModelScope.launch {
            val detail = repository.getHuecoDetail(id)
            _huecoDetail.value = detail
            // actualizar selección de confirmación según lo que traiga el detalle
            _selectedConfirmation.value = detail.miConfirmacion?.nuevoEstado
            // Si aún no tenemos comentarios locales y el detalle trae comentarios, inicializamos los primeros 3
            if (_comentarios.value.isEmpty() && !detail.comentarios.isNullOrEmpty()) {
                _comentarios.value = detail.comentarios.sortedByDescending { it.fecha ?: "" }.take(3)
                // actualizar conteo si el detalle trae total
                if (detail.totalComentarios != null) _comentariosCount.value = detail.totalComentarios
            }
        }
    }

    fun loadComentarios(huecoId: Int, page: Int = 1, pageSize: Int = 10) {
        if (isLoading || isLastPage) return
        isLoading = true
        viewModelScope.launch {
            val response = repository.getComentarios(huecoId, page, pageSize)
            if (page == 1) {
                // ordenar por fecha descendente para tener los más recientes primero
                _comentarios.value = response.results.sortedByDescending { it.fecha ?: "" }
                // actualizar el conteo total cuando se obtiene la página
                _comentariosCount.value = response.count
            } else {
                _comentarios.value = (_comentarios.value + response.results).sortedByDescending { it.fecha ?: "" }
            }
            currentPage = page
            isLastPage = response.next == null
            isLoading = false
        }
    }

    fun loadNextComentariosPage(id: Int, pageSize: Int = 10) {
        if (!isLastPage && !isLoading) {
            loadComentarios(id, currentPage + 1, pageSize)
        }
    }

    fun resetComentarios() {
        currentPage = 1
        isLastPage = false
        _comentarios.value = emptyList()
        _comentariosCount.value = null
    }

    fun toggleFollow(huecoId: Int, isFollowed: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleFollow(huecoId, isFollowed)
            if (result) {
                _huecoDetail.value = _huecoDetail.value?.copy(isFollowed = !isFollowed)
            }
        }
    }

    fun postComentario(huecoId: Int, texto: String, imagen: String? = null) {
        if (isPostingComentario) return
        isPostingComentario = true
        viewModelScope.launch {
            try {
                val request = CreateComentarioRequest(hueco = huecoId, texto = texto, imagen = imagen)
                val created = repository.createComentario(request)
                // Añadir al final de la lista actual sin refrescar
                // Insertar y reordenar por fecha (por si el server devuelve fecha distinta)
                _comentarios.value = (listOf(created) + _comentarios.value).sortedByDescending { it.fecha ?: "" }
                // Actualizar conteo si ya lo conocíamos
                _comentariosCount.value = (_comentariosCount.value ?: 0) + 1
                // Actualizar huecoDetail si existe para reflejar nuevo comentario en el detalle
                _huecoDetail.value = _huecoDetail.value?.let { current ->
                    val existing = current.comentarios ?: emptyList()
                    current.copy(comentarios = existing + created) // keep backend order if used elsewhere
                }
            } catch (e: Exception) {
                // manejar error (log/mostrar mensaje) - por ahora no hacemos nada
            } finally {
                isPostingComentario = false
            }
        }
    }

    fun validarHuecoExiste(huecoId: Int) {
        viewModelScope.launch {
            // Optimistic: ocultar botones de validación inmediatamente
            _selectedValidation.value = null
            _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = true)

            when (val result = huecoRepository.validarHueco(huecoId, true)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val resp = result.data
                    // Guardar la confirmación recibida; validadoUsuario ya fue marcado optimistamente
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                            id = resp.id,
                            hueco = resp.hueco,
                            usuario = resp.usuario,
                            usuarioNombre = resp.usuarioNombre,
                            confirmado = null,
                            fecha = resp.fecha,
                            nuevoEstado = null
                        ),
                        validadoUsuario = true
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                    // Revertir optimistic update si la validación falló
                    _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = false)
                    // Si el servidor dice que ya validó, mantener validadoUsuario=true
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = true)
                    }
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                    // Revertir optimistic update en caso de error de red
                    _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = false)
                }
            }
        }
    }

    fun validarHuecoNoExiste(huecoId: Int) {
        viewModelScope.launch {
            // Optimistic: ocultar botones de validación inmediatamente
            _selectedValidation.value = null
            _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = true)

            when (val result = huecoRepository.validarHueco(huecoId, false)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val resp = result.data
                    // Guardar la confirmación recibida; validadoUsuario ya fue marcado optimistamente
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                            id = resp.id,
                            hueco = resp.hueco,
                            usuario = resp.usuario,
                            usuarioNombre = resp.usuarioNombre,
                            confirmado = null,
                            fecha = resp.fecha,
                            nuevoEstado = null
                        ),
                        validadoUsuario = true
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                    // Revertir optimistic update si la validación falló
                    _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = false)
                    if (result.message?.contains("Ya has validado este hueco") == true) {
                        _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = true)
                    }
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                    // Revertir optimistic update en caso de error de red
                    _huecoDetail.value = _huecoDetail.value?.copy(validadoUsuario = false)
                }
            }
        }
    }

    // Permite limpiar selección local (por ejemplo al recargar detalle)
    fun clearLocalValidationSelection() {
        _selectedValidation.value = null
    }

    fun confirmarEstado(huecoId: Int, nuevoEstado: Int) {
        if (_isConfirming.value) return
        _isConfirming.value = true
        // marcar selección local inmediatamente
        _selectedConfirmation.value = nuevoEstado
        viewModelScope.launch {
            when (val res = huecoRepository.confirmarHueco(huecoId, nuevoEstado)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val conf = res.data
                    // Actualizar miConfirmacion localmente con la respuesta del servidor,
                    // pero NO cambiar el campo `estado` del huecoDetail aquí. El estado real
                    // se actualizará cuando el backend propague el cambio y el detalle se recargue.
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        miConfirmacion = com.fredrueda.huecoapp.feature.report.data.remote.dto.MiConfirmacionResponse(
                            id = conf.id,
                            hueco = conf.hueco,
                            usuario = conf.usuario,
                            usuarioNombre = conf.usuarioNombre,
                            confirmado = null,
                            fecha = conf.fecha,
                            nuevoEstado = conf.nuevoEstado
                        )
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                    // manejar error (opcional: revertir selección local)
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                    // manejar error de red (opcional: revertir selección local)
                }
            }
            _isConfirming.value = false
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
}
