package com.pepper.care.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.common.repo.PatientRepository
import org.joda.time.DateTime

interface GetPatientBirthdayUseCase {
    suspend operator fun invoke() : AppResult<DateTime>
}

class GetPatientBirthdayUseCaseUsingRepository(
    private val patientRepository: PatientRepository
) : GetPatientBirthdayUseCase {

    override suspend fun invoke() : AppResult<DateTime> {
        return patientRepository.fetchBirthDate()
    }
}