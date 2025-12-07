package com.fredrueda.huecoapp.feature.home.domain.usecase

import com.fredrueda.huecoapp.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeHuecosUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    class GetMisReportesUseCase @Inject constructor(private val repo: HomeRepository) {
        suspend operator fun invoke(limit: Int, offset: Int) = repo.getMisReportes(limit, offset)
    }

    class GetSeguidosUseCase @Inject constructor(private val repo: HomeRepository) {
        suspend operator fun invoke(limit: Int, offset: Int) = repo.getSeguidos(limit, offset)
    }

}
