package com.pepper.care.order.repo

import com.pepper.care.common.AppResult
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.handleApiError
import com.pepper.care.common.handleSuccess
import com.pepper.care.common.repo.AppPreferencesRepository

interface OrderRepository {
    suspend fun fetchMeals() : AppResult<List<PlatformMealsResponse>>
    suspend fun addOrder(meal: String)
}

class OrderRepositoryImpl(
    private val api: PlatformApi
) : OrderRepository {

    override suspend fun fetchMeals(): AppResult<List<PlatformMealsResponse>> {
        val mealsList = ArrayList<PlatformMealsResponse>()

        val response = api.getAvailableMeals()

        if (response.isSuccessful){
            response.body()?.let {
                mealsList.addAll(it)
            }
            handleSuccess(response)
        } else {
            handleApiError(response)
        }
        return AppResult.Success(mealsList)
    }

    override suspend fun addOrder(meal: String) {
        val formatted = "BOT:3:PATIENT:3:MEAL_CHOSEN#{${meal}}"
    }
}