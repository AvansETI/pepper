package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.entities.RecyclerAdapterItem

interface OrderViewModel {
    fun onStart()
    fun navigateToDetailScreen(view: View)

    val orderText: String
    val currentSelectedItem: MutableLiveData<PlatformMealsResponse>
    val adapterClickedListener: ClickCallback<RecyclerAdapterItem>
    val mealsList: MutableLiveData<List<RecyclerAdapterItem>>
    val errorList: MutableLiveData<List<RecyclerAdapterItem>>


    val viewMealTitle: String
    val viewMealDescription: String
    val viewMealSource: String
    val viewMealType: String
    val viewMealAllergies: String
    val viewMealCalories: String
}