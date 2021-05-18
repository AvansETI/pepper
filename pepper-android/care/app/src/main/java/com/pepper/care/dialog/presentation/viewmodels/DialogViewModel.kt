package com.pepper.care.dialog.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.views.FabCallback
import com.pepper.care.feedback.FeedbackCallback

interface DialogViewModel {
    val bottomText: MutableLiveData<String>
    val isNextButtonVisible: MutableLiveData<Boolean>
    val fabType: MutableLiveData<FabType>
    val nextCallback: FabCallback

    fun updateDataBasedOnRoute(type: DialogRoutes)
}