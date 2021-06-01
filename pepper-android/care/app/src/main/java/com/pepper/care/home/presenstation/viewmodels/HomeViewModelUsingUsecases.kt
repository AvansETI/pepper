package com.pepper.care.home.presenstation.viewmodels

import androidx.lifecycle.ViewModel

class HomeViewModelUsingUsecases : ViewModel(), HomeViewModel {
    override val standbyText: String = "Pepper staat momenteel op standby..."
}