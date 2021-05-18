package com.pepper.care.order.common.usecases

import com.pepper.care.common.AppResult
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.order.repo.OrderRepository

interface GetPlatformMealsUseCase {
    suspend operator fun invoke() : AppResult<List<PlatformMealsResponse>>
}

class GetPlatformMealsUseCaseUsingRepository(
    private val repository: OrderRepository
) : GetPlatformMealsUseCase {

    override suspend fun invoke(): AppResult<List<PlatformMealsResponse>> {
        return repository.fetchMeals()
    }
}