package com.pepper.care.dialog.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.dialog.repo.AvailableScreenRepository

interface GetAvailableScreensUseCase {
    suspend operator fun invoke() : AppResult<IntArray>
}

class GetAvailableScreensUseCaseUsingRepository(
    private val patientRepository: AvailableScreenRepository
) : GetAvailableScreensUseCase {

    override suspend fun invoke() : AppResult<IntArray> {
        return patientRepository.fetchScreens()
    }
}