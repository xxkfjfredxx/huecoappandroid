package com.fredrueda.huecoapp.feature.report.domain.usecase

import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import javax.inject.Inject

class GetHuecosCercanosUseCase @Inject constructor(
    private val huecoRepository: HuecoRepository
) {
    suspend operator fun invoke(
        latitud: Double,
        longitud: Double,
        radio: Int
    ) = huecoRepository.getHuecosCercanos(
        latitud = latitud,
        longitud = longitud,
        radio = radio
    )

}