package com.pepper.care.order.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformMeal
import com.pepper.care.order.repo.OrderRepository
import kotlinx.coroutines.flow.Flow

interface GetPlatformMealsUseCase {
    suspend operator fun invoke() : Flow<List<PlatformMeal>>
}

class GetPlatformMealsUseCaseUsingRepository(
    private val repository: OrderRepository
) : GetPlatformMealsUseCase {

    override suspend fun invoke(): Flow<List<PlatformMeal>> {
        return repository.fetchMeals()
    }
}