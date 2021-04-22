package com.pepper.care.home.presenstation.viewmodels

import android.view.View

interface HomeViewModel {
    val todaysDateText: String
    fun onButtonClick(view: View)
}