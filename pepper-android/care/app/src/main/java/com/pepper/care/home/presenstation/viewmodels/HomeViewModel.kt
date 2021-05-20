package com.pepper.care.home.presenstation.viewmodels

import android.view.View

interface HomeViewModel {
    fun goToNextScreen(view: View)
    val standbyText: String
    val buttonText: String
}