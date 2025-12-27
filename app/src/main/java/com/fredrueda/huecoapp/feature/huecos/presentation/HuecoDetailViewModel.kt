package com.fredrueda.huecoapp.feature.huecos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrueda.huecoapp.feature.huecos.data.repository.HuecoDetailRepository
import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HuecoDetailViewModel @Inject constructor(
    private val repository: HuecoDetailRepository
) : ViewModel() {
    private val _huecoDetail = MutableStateFlow<HuecoResponse?>(null)
    val huecoDetail: StateFlow<HuecoResponse?> = _huecoDetail

    private val _comentarios = MutableStateFlow<List<ComentarioResponse>>(emptyList())
    val comentarios: StateFlow<List<ComentarioResponse>> = _comentarios

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    fun loadHuecoDetail(id: Int) {
        viewModelScope.launch {
            _huecoDetail.value = repository.getHuecoDetail(id)
        }
    }

    fun loadComentarios(huecoId: Int, page: Int = 1, pageSize: Int = 10) {
        if (isLoading || isLastPage) return
        isLoading = true
        viewModelScope.launch {
            val response = repository.getComentarios(huecoId, page, pageSize)
            if (page == 1) {
                _comentarios.value = response.results
            } else {
                _comentarios.value = _comentarios.value + response.results
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
    }
}
