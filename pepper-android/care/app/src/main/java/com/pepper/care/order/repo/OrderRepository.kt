package com.pepper.care.order.repo

import android.content.SharedPreferences
import com.pepper.care.common.AppResult
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_PUBLISH_MSG_KEY
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.handleApiError
import com.pepper.care.common.handleSuccess

interface OrderRepository {
    suspend fun fetchMeals() : AppResult<List<PlatformMealsResponse>>
    suspend fun addOrder(meal: String)
}

class OrderRepositoryImpl(
    private val api: PlatformApi,
    private val sharedPreferencesEditor: SharedPreferences.Editor
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
        sharedPreferencesEditor.putString(COMMON_SHARED_PREF_PUBLISH_MSG_KEY, meal).commit()
    }
}