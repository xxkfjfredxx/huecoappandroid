package com.fredrueda.huecoapp.feature.home.data.remote.dto

import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse

fun HuecoHomeDto.toHuecoResponse(): HuecoResponse = HuecoResponse(
    id = id,
    usuario = usuario,
    usuarioNombre = usuarioNombre,
    ciudad = null,
    descripcion = descripcion,
    latitud = latitud,
    longitud = longitud,
    estado = estado,
    fechaReporte = fechaReporte,
    fechaActualizacion = fechaActualizacion,
    numeroCiclos = numeroCiclos,
    validacionesPositivas = validacionesPos,
    validacionesNegativas = validacionesNeg,
    gravedad = null,
    vistas = null,
    imagen = imagen,
    comentarios = emptyList(),
    totalComentarios = 0,
    confirmacionesCount = confirmacionesCount,
    validadoUsuario = null,
    miConfirmacion = miConfirmacion,
    faltanValidaciones = null,
    isFollowed = null
)
