package com.pepper.care.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.common.entities.PatientDetails
import com.pepper.care.common.repo.PlatformPatientDetailsRepository

interface GetPatientDetailsUseCase {
    suspend operator fun invoke() : AppResult<PatientDetails>
}

class GetPatientDetailsUseCaseUsingRepository(
    private val platformPatientDetailsRepository: PlatformPatientDetailsRepository
) : GetPatientDetailsUseCase {

    override suspend fun invoke() : AppResult<PatientDetails> {
        return platformPatientDetailsRepository.fetchDetails()
    }
}