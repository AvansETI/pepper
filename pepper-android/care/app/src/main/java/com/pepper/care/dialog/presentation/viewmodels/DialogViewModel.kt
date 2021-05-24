package com.pepper.care.dialog.presentation.viewmodels

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.pepper.care.common.DialogCallback
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.views.FabCallback
import com.pepper.care.feedback.FeedbackCallback

interface DialogViewModel {
    val bottomText: MutableLiveData<String>
    val isNextButtonVisible: MutableLiveData<Boolean>
    val fabType: MutableLiveData<FabType>
    val fabCallback: FabCallback

    val inputTextWatcher: TextWatcher
    val inputTextLength: MutableLiveData<Int>
    val isKeyboardVisible: MutableLiveData<Boolean>
    val isKeyboardNumeric: MutableLiveData<Boolean>
    val inputText: MutableLiveData<String>
    val keyboardKeyListener: View.OnKeyListener

    fun updateDataBasedOnRoute(type: DialogRoutes)
}