package com.fredrueda.huecoapp.ui.deeplink

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor() : ViewModel() {

    private val _uid = MutableStateFlow<String?>(null)
    val uid = _uid.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    fun setDeepLink(uid: String?, token: String?) {
        _uid.value = uid
        _token.value = token
    }
}
