package com.fredrueda.huecoapp.feature.report.domain.usecase

import com.fredrueda.huecoapp.feature.report.domain.repository.HuecoRepository
import javax.inject.Inject

class ReportarHuecoUseCase @Inject constructor(
    private val repository: HuecoRepository
) {
    suspend operator fun invoke(
        huecoId: Int,
        motivo: String,
        comentario: String
    ) = repository.reportarHueco(huecoId, motivo, comentario)
}
