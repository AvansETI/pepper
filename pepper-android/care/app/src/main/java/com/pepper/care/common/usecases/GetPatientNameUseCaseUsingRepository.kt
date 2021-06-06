package com.pepper.care.common.usecases

import com.pepper.care.common.repo.PatientRepository
import kotlinx.coroutines.flow.Flow

interface GetPatientNameUseCase {
    suspend operator fun invoke() : Flow<String>
}

class GetPatientNameUseCaseUsingRepository(
    private val patientRepository: PatientRepository
) : GetPatientNameUseCase {

    override suspend fun invoke() : Flow<String> {
        return patientRepository.fetchName()
    }
}