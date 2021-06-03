package com.pepper.care.feedback.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity
import com.pepper.care.feedback.presentation.FeedbackFragment
import com.ramotion.fluidslider.FluidSlider

interface FeedbackViewModel {
    val headerText: MutableLiveData<String>
    val sliderRange: Pair<Int, Int>
    val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage>
    val imageListener: FeedbackCallback
    val fluidSlider: MutableLiveData<FluidSlider>

    fun onStart()

}