package com.fredrueda.huecoapp.feature.huecos.presentation

// Formatea una fecha ISO o similar a "YYYY-MM-DD HH:mm" de manera simple.
fun formatDateShort(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return ""
    try {
        // Manejar formatos como: 2025-12-31T03:56:22.682089Z o 2025-12-31T03:56:22Z
        val parts = dateStr.split('T')
        if (parts.size >= 2) {
            val datePart = parts[0]
            val timePart = parts[1]
            // timePart posiblemente contenga zona 'Z' o fracciones
            val hhmm = timePart.takeWhile { it != 'Z' }.takeWhile { it != '+' }.trim().take(5)
            return "$datePart $hhmm"
        }
        // Si no contiene T, intentar separar por espacio
        val sp = dateStr.split(' ')
        if (sp.size >= 2) {
            val datePart = sp[0]
            val timePart = sp[1]
            return "$datePart ${timePart.take(5)}"
        }
        // Fallback: devolver el original truncado
        return dateStr.take(16)
    } catch (_: Exception) {
        return dateStr
    }
}

// Normaliza un valor de estado (puede venir como Int, String numérico o texto) a la representación interna usada en la app
fun mapEstadoValueToInternal(estadoRaw: Any?): String {
    if (estadoRaw == null) return "pendiente_validacion"
    when (estadoRaw) {
        is Number -> return mapEstadoIntToString(estadoRaw.toInt())
        is String -> {
            val num = estadoRaw.toIntOrNull()
            if (num != null) return mapEstadoIntToString(num)
            // Si es un texto, normalizarlo a minúsculas y comparar
            return when (estadoRaw.lowercase()) {
                "pendiente_validacion", "pendiente" -> "pendiente_validacion"
                "activo", "active" -> "activo"
                "reabierto", "reincidente" -> "reabierto"
                "cerrado", "arreglado", "closed" -> "cerrado"
                "en_reparacion", "en reparacion", "en reparación" -> "en_reparacion"
                "reparado", "fixed" -> "reparado"
                "rechazado", "rejected" -> "rechazado"
                else -> estadoRaw.lowercase()
            }
        }
        else -> return estadoRaw.toString().lowercase()
    }
}

// Convierte el int de estado a la cadena interna
fun mapEstadoIntToString(estado: Int): String {
    return when (estado) {
        1 -> "pendiente_validacion"
        2 -> "activo"
        3 -> "rechazado"
        4 -> "reabierto"
        5 -> "cerrado"
        6 -> "en_reparacion"
        7 -> "reparado"
        else -> "pendiente_validacion"
    }
}

// Obtener etiqueta legible para la UI desde cualquier valor
fun mapEstadoValueToLabel(estadoRaw: Any?): String {
    val internal = mapEstadoValueToInternal(estadoRaw)
    return when (internal) {
        "pendiente_validacion" -> "Pendiente"
        "activo" -> "Activo"
        "rechazado" -> "Rechazado"
        "reabierto" -> "Reincidente"
        "cerrado" -> "Arreglado"
        "en_reparacion" -> "En reparación"
        "reparado" -> "Reparado"
        else -> internal.replaceFirstChar { it.uppercase() }
    }
}
