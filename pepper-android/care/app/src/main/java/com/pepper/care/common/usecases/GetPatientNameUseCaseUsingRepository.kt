package com.pepper.care.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.common.repo.PatientRepository

interface GetPatientNameUseCase {
    suspend operator fun invoke() : AppResult<String>
}

class GetPatientNameUseCaseUsingRepository(
    private val patientRepository: PatientRepository
) : GetPatientNameUseCase {

    override suspend fun invoke() : AppResult<String> {
        return patientRepository.fetchName()
    }
}