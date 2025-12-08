package com.fredrueda.huecoapp.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.home.domain.mapper.toHomeItem
import com.fredrueda.huecoapp.feature.home.domain.usecase.GetHomeHuecosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMisReportesUseCase: GetHomeHuecosUseCase.GetMisReportesUseCase,
    private val getSeguidosUseCase: GetHomeHuecosUseCase.GetSeguidosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    private val pageSize = 10

    // CARGA INICIAL (llama a ambos servicios 1 sola vez)
    fun loadInitial() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            cargarMisReportes(reset = true)
            cargarSeguidos(reset = true)

            _state.value = _state.value.copy(
                isLoading = false
            )
        }
    }

    fun refreshMisReportes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
                error = null
            )

            cargarMisReportes(reset = true)

            _state.value = _state.value.copy(
                isRefreshing = false
            )
        }
    }

    fun refreshSeguidos() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isRefreshing = true,
                error = null
            )

            cargarSeguidos(reset = true)

            _state.value = _state.value.copy(
                isRefreshing = false
            )
        }
    }

    // INFINITE SCROLL SOLO PARA SEGUIDOS
    fun loadMoreSeguidos() {
        val current = _state.value
        if (current.isLoadingMoreSeguidos || !current.seguidosHasMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMoreSeguidos = true)

            cargarSeguidos(reset = false)

            _state.value = _state.value.copy(isLoadingMoreSeguidos = false)
        }
    }

    // ------------------ PRIVADOS ------------------

    private suspend fun cargarMisReportes(reset: Boolean) {
        val current = _state.value
        val offset = if (reset) 0 else current.misReportesOffset + pageSize

        when (val r = getMisReportesUseCase(limit = pageSize, offset = offset)) {
            is ApiResponse.Success -> {
                val nuevos = r.data.results.map { it.toHomeItem("Reportado") }

                val listaFinal = if (reset) {
                    nuevos
                } else {
                    current.misReportes + nuevos
                }

                val hayMas = r.data.next != null

                _state.value = _state.value.copy(
                    misReportes = listaFinal,
                    misReportesOffset = offset,
                    misReportesHasMore = hayMas
                )
            }
            is ApiResponse.HttpError -> {
                _state.value = _state.value.copy(error = r.message)
            }
            is ApiResponse.NetworkError -> {
                _state.value = _state.value.copy(error = r.throwable.message)
            }
        }
    }

    private suspend fun cargarSeguidos(reset: Boolean) {
        val current = _state.value
        val offset = if (reset) 0 else current.seguidosOffset + pageSize

        when (val r = getSeguidosUseCase(limit = pageSize, offset = offset)) {
            is ApiResponse.Success -> {
                val nuevos = r.data.results.map { it.toHomeItem("Seguido") }

                val listaFinal = if (reset) {
                    nuevos
                } else {
                    current.seguidos + nuevos
                }

                val hayMas = r.data.next != null

                _state.value = _state.value.copy(
                    seguidos = listaFinal,
                    seguidosOffset = offset,
                    seguidosHasMore = hayMas
                )
            }
            is ApiResponse.HttpError -> {
                _state.value = _state.value.copy(error = r.message)
            }
            is ApiResponse.NetworkError -> {
                _state.value = _state.value.copy(error = r.throwable.message)
            }
        }
    }

    fun loadMoreMisReportes() {
        val current = _state.value
        if (current.isLoadingMoreMisReportes || !current.misReportesHasMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMoreMisReportes = true)

            cargarMisReportes(reset = false)

            _state.value = _state.value.copy(isLoadingMoreMisReportes = false)
        }
    }


}

