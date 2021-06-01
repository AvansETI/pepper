package com.pepper.care.home.presenstation.viewmodels

import android.view.View

interface HomeViewModel {
    fun onStart(view: View)
    val standbyText: String
}