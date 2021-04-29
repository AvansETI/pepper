package com.pepper.care.common.repo

import android.util.Log
import com.pepper.care.common.AppResult
import com.pepper.care.common.handleSuccess
import com.pepper.care.common.handleApiError
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.entities.PlatformMealsResponse

interface PlatformMealsRepository {
    suspend fun fetchMeals() : AppResult<List<PlatformMealsResponse>>
}

class PlatformMealsRepositoryImpl(
    private val api: PlatformApi
) : PlatformMealsRepository {

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
}