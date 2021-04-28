package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.entities.RecyclerAdapterItem

interface OrderViewModel {
    fun onStart()
    fun onBackPress(view: View)

    val orderText: String
    val adapterClickedListener: ClickCallback<RecyclerAdapterItem>
    val mealsList: MutableLiveData<List<RecyclerAdapterItem>>
    val errorList: MutableLiveData<List<RecyclerAdapterItem>>

    val meal: MutableLiveData<PlatformMealsResponse>
}