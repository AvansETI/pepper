package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.UpdateNotifierCallback
import com.pepper.care.order.common.view.MealSliderItem
import com.pepper.care.order.common.view.SliderAdapterItem
import com.ramotion.cardslider.CardSnapHelper

interface OrderViewModel {
    fun onStart()
    fun onBackPress(view: View)

    val recyclerVisibility: MutableLiveData<Boolean>
    val progressVisibility: MutableLiveData<Boolean>
    val isLoadedSuccessfully: MutableLiveData<Boolean>

    val adapterClickedListener: ClickCallback<SliderAdapterItem>
    val recyclerList: MutableLiveData<ArrayList<SliderAdapterItem>>
    val notifyCallback: UpdateNotifierCallback

    val meal: MutableLiveData<MealSliderItem>

    val currentPosition: MutableLiveData<Int>
    val isOpposite: MutableLiveData<Boolean>
    val recyclerScrollListener: RecyclerView.OnScrollListener
    val cardSnapHelper: CardSnapHelper

    val titleText: MutableLiveData<Pair<String, Boolean>>
    val labelText: MutableLiveData<Pair<String, Boolean>>
    val descriptionText: MutableLiveData<Pair<String, Boolean>>
    val allergiesText: MutableLiveData<Pair<String, Boolean>>

    val switcherTitleResource: Int
    val switcherLabelResource: Int
    val switcherDescriptionResource: Int
    val switcherAllergiesResource: Int
}