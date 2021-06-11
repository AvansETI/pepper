package com.pepper.care.order.common.usecases

import com.pepper.care.order.repo.OrderRepository

interface AddPatientFoodChoiceUseCase {
    suspend operator fun invoke(meal: String)
}

class AddPatientFoodChoiceUseCaseUsingRepository(
    private val repository: OrderRepository
) : AddPatientFoodChoiceUseCase {

    override suspend fun invoke(meal: String) {
        repository.addOrder(meal)
    }
}