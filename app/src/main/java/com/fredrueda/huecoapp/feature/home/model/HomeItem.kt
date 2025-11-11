package com.fredrueda.huecoapp.feature.home.model

data class HomeItem(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val estado: String, // Pendiente, En reparación, Arreglado
    val fecha: String,
    val tipo: String // "Reportado", "Siguiendo", "Reincidente"
)