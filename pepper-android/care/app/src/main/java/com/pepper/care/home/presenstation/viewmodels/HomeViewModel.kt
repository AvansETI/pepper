package com.pepper.care.home.presenstation.viewmodels

import android.view.View

interface HomeViewModel {
    fun goToNextScreen(view: View)
    fun onCreated()

    val standbyText: String
    val buttonText: String
}