package com.pepper.care.order.common.usecases

import com.pepper.care.core.services.platform.entities.PlatformMeal
import com.pepper.care.order.repo.OrderRepository
import kotlinx.coroutines.flow.StateFlow

interface GetPlatformMealsUseCase {
    suspend operator fun invoke() : StateFlow<List<PlatformMeal>>
}

class GetPlatformMealsUseCaseUsingRepository(
    private val repository: OrderRepository
) : GetPlatformMealsUseCase {

    override suspend fun invoke(): StateFlow<List<PlatformMeal>> {
        return repository.fetchMeals()
    }
}