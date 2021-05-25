package com.pepper.care.feedback.presentation.viewmodels

import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.DialogCallback
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.views.FabCallback
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity
import com.ramotion.fluidslider.FluidSlider

interface FeedbackViewModel {
    val headerText: String

    val sliderRange: Pair<Int, Int>
    val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage>
    val imageListener: FeedbackCallback

    val isNextButtonVisible: MutableLiveData<Boolean>
    val fabType: MutableLiveData<FabType>
    val fabCallback: FabCallback

    fun onStart()

    val inputTextWatcher: TextWatcher
    val inputTextLength: MutableLiveData<Int>
    val isKeyboardVisible: MutableLiveData<Boolean>
    val isKeyboardNumeric: MutableLiveData<Boolean>
    val inputText: MutableLiveData<String>
    val keyboardKeyListener: View.OnKeyListener
}