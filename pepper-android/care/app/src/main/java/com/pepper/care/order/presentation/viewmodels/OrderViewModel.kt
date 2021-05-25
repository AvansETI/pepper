package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.entities.RecyclerAdapterItem

interface OrderViewModel {
    fun onStart()
    fun onBackPress(view: View)

    val recyclerVisibility: MutableLiveData<Boolean>
    val progressVisibility: MutableLiveData<Boolean>
    val recyclerSpanCount: MutableLiveData<Int>

    val orderText: String
    val adapterClickedListener: ClickCallback<RecyclerAdapterItem>
    val recyclerList: MutableLiveData<List<RecyclerAdapterItem>>

    val meal: MutableLiveData<PlatformMealsResponse>

    val buttonDetailText: String
    fun goToNextScreen(view: View)
}