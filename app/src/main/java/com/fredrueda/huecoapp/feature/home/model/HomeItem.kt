package com.fredrueda.huecoapp.feature.home.model

import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

data class HomeItem(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val estado: String, // Pendiente, En reparación, Arreglado
    val fecha: String,
    val tipo: String, // "Reportado", "Siguiendo", "Reincidente"
    val imagen: String?
)

fun HomeItem.toHuecoResponse(): HuecoResponse = HuecoResponse(
    id = id,
    usuario = null,
    usuarioNombre = null,
    ciudad = null,
    descripcion = descripcion,
    latitud = null,
    longitud = null,
    estado = estado,
    fechaReporte = fecha,
    fechaActualizacion = null,
    numeroCiclos = null,
    validacionesPositivas = null,
    validacionesNegativas = null,
    gravedad = null,
    vistas = null,
    imagen = imagen,
    comentarios = emptyList<ComentarioResponse>(),
    totalComentarios = 0,
    confirmacionesCount = null,
    validadoUsuario = null,
    miConfirmacion = null,
    faltanValidaciones = null,
    isFollowed = null
)
