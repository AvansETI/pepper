package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.ClickCallback
import com.pepper.care.order.common.view.MealSliderItem
import com.pepper.care.order.common.view.SliderAdapterItem

interface OrderViewModel {
    fun onStart()
    fun onBackPress(view: View)

    val recyclerVisibility: MutableLiveData<Boolean>
    val progressVisibility: MutableLiveData<Boolean>
    val isLoadedSuccessfully: MutableLiveData<Boolean>
    val recyclerSpanCount: MutableLiveData<Int>

    val adapterClickedListener: ClickCallback<SliderAdapterItem>
    val recyclerList: MutableLiveData<ArrayList<SliderAdapterItem>>

    val meal: MutableLiveData<MealSliderItem>
}