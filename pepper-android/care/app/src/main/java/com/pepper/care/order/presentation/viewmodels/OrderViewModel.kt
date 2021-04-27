package com.pepper.care.order.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.RecyclerAdapterItem

interface OrderViewModel {
    fun onStart()

    val orderText: String
    val adapterClickedListener: ClickCallback<RecyclerAdapterItem>
    val mealsList: MutableLiveData<List<RecyclerAdapterItem>>
    val errorList: MutableLiveData<List<RecyclerAdapterItem>>
}