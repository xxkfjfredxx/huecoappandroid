package com.fredrueda.huecoapp.feature.home.data.remote.dto

import com.fredrueda.huecoapp.feature.report.data.remote.dto.ComentarioResponse
import com.fredrueda.huecoapp.feature.report.data.remote.dto.HuecoResponse
import kotlin.math.max

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
    comentarios = comentarios?.map { c ->
        ComentarioResponse(
            id = c.id,
            usuario = c.usuario,
            usuarioNombre = c.usuarioNombre,
            texto = c.texto,
            imagen = c.imagen,
            fecha = c.fecha
        )
    } ?: emptyList(),
    // Priorizar el contador que venga de la API (total_comentarios). Si no viene usar el tamaño del arreglo.
    totalComentarios = totalComentarios ?: comentarios?.size ?: 0,
    confirmacionesCount = confirmacionesCount,
    validadoUsuario = validadoUsuario,
    miConfirmacion = miConfirmacion,
    // mapear faltanValidaciones si vino del DTO; si no viene, calcularlo a partir de validaciones positivas
    faltanValidaciones = faltanValidaciones ?: max(0, 5 - (validacionesPos ?: 0)),
    isFollowed = null
)
