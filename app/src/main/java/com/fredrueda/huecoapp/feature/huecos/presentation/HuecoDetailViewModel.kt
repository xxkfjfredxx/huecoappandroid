package com.fredrueda.huecoapp.feature.huecos.presentation

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
        // Solo inicializar si aún no tenemos detalle cargado
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
            // no tocamos _comentariosCount: lo dejamos null hasta que hagamos la carga completa
        }
    }

    fun loadHuecoDetail(id: Int) {
        viewModelScope.launch {
            val detail = repository.getHuecoDetail(id)
            _huecoDetail.value = detail
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
            when (val result = huecoRepository.validarHueco(huecoId, true)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val resp = result.data
                    // actualizar huecoDetail
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        validadoUsuario = true,
                        miConfirmacion = resp,
                        validacionesPositivas = (_huecoDetail.value?.validacionesPositivas ?: 0) + 1,
                        faltanValidaciones = maxOf(0, 5 - ((_huecoDetail.value?.validacionesPositivas ?: 0) + 1))
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                    // manejar errores según mensaje
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                    // manejar error de red
                }
            }
        }
    }

    fun validarHuecoNoExiste(huecoId: Int) {
        viewModelScope.launch {
            when (val result = huecoRepository.validarHueco(huecoId, false)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val resp = result.data
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        validadoUsuario = true,
                        miConfirmacion = resp,
                        validacionesNegativas = (_huecoDetail.value?.validacionesNegativas ?: 0) + 1
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                }
            }
        }
    }

    fun confirmarEstado(huecoId: Int, nuevoEstado: Int) {
        if (_isConfirming.value) return
        _isConfirming.value = true
        viewModelScope.launch {
            when (val res = huecoRepository.confirmarHueco(huecoId, nuevoEstado)) {
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.Success -> {
                    val conf = res.data
                    // actualizar huecoDetail
                    _huecoDetail.value = _huecoDetail.value?.copy(
                        estado = mapEstadoIntToString(conf.nuevoEstado)
                    )
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.HttpError -> {
                    // manejar error
                }
                is com.fredrueda.huecoapp.core.data.network.ApiResponse.NetworkError -> {
                    // manejar error de red
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
