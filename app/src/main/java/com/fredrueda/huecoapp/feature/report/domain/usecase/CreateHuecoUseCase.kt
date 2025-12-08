package com.fredrueda.huecoapp.feature.report.domain.use_case

import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import java.io.File
import javax.inject.Inject

class CreateHuecoUseCase @Inject constructor(
    private val repository: HuecoRepository
) {

    suspend operator fun invoke(
        latitud: Double,
        longitud: Double,
        descripcion: String,
        imagen: File?
    ) = repository.crearHueco(
        latitud = latitud,
        longitud = longitud,
        descripcion = descripcion,
        imagen = imagen
    )
}
