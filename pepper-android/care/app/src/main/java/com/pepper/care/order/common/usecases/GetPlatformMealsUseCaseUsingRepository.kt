package com.pepper.care.order.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.repo.PlatformMealsRepository

interface GetPlatformMealsUseCase {
    suspend operator fun invoke() : AppResult<List<PlatformMealsResponse>>
}

class GetPlatformMealsUseCaseUsingRepository(
    private val platformMealsRepository: PlatformMealsRepository
) : GetPlatformMealsUseCase {

    override suspend fun invoke(): AppResult<List<PlatformMealsResponse>> {
        return platformMealsRepository.fetchMeals()
    }
}