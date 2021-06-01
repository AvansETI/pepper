package com.pepper.care.dialog.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import com.pepper.care.dialog.DialogRoutes

interface DialogViewModel {
    val bottomText: MutableLiveData<String>
    val inputText: MutableLiveData<String>

    fun updateDataBasedOnRoute(type: DialogRoutes)
}