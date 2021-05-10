package com.pepper.care.dialog.presentation.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.pepper.care.dialog.DialogRoutes

interface DialogViewModel {
    val bottomText: MutableLiveData<String>
    fun updateDataBasedOnRoute(type: DialogRoutes)
    fun onConfirmResult(view: View)
}