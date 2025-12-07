package com.fredrueda.huecoapp.feature.home.data.remote.dto

data class PagedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
